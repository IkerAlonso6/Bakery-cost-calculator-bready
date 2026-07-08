# Database Schema - BakeryCostCalculator

Schema de PostgreSQL para la base de datos.

> El cálculo de costos se define en `COSTING_MODEL.md`. Este documento solo describe la **persistencia**. Los cálculos se hacen en memoria (dominio Java), no en la base.

---

## SQL completo

```sql
-- =============================================================
-- BakeryCostCalculator - Database Schema
-- PostgreSQL
-- =============================================================

-- Conexión
-- Server: localhost
-- Port: 5432
-- Database: bakery_cost_calculator
-- User: postgres

-- =============================================================
-- Tabla: units_of_measurement
-- Propósito: Referencia de unidades de medida soportadas
-- =============================================================
CREATE TABLE IF NOT EXISTS units_of_measurement (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- =============================================================
-- Tabla: inputs
-- Propósito: Materia prima que se compra
-- Reglas:
--   - Un insumo no puede tener el mismo nombre que otro
--   - El precio se puede actualizar cuando fluctúa el mercado
--   - Debe tener siempre una unidad de medida
-- =============================================================
CREATE TABLE IF NOT EXISTS inputs (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    unit_of_measurement_id SMALLINT NOT NULL REFERENCES units_of_measurement(id)
);

-- =============================================================
-- Tabla: recipes
-- Propósito: Define cómo se produce un producto
-- Reglas:
--   - El nombre debe ser único
--   - Una receta puede tener múltiples ingredientes
--   - Una receta puede usarse en múltiples productos
--   - Rinde una cantidad (kg o unidades) por lote -> permite costo por unidad
-- =============================================================
CREATE TABLE IF NOT EXISTS recipes (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    yield_quantity NUMERIC(10, 4) NOT NULL,               -- cuánto rinde el lote
    yield_unit_id SMALLINT NOT NULL REFERENCES units_of_measurement(id)  -- kg o unidad
);

-- =============================================================
-- Tabla: ingredients
-- Propósito: Uso de un insumo dentro de una receta con cantidad específica
-- Reglas:
--   - Un ingrediente siempre pertenece a una receta
--   - Un insumo no puede repetirse en la misma receta (UNIQUE)
--   - La cantidad expresa en la misma unidad que el insumo
-- Relación: Tabla de composición, no existe sin Recipe
-- =============================================================
CREATE TABLE IF NOT EXISTS ingredients (
    id SMALLINT NOT NULL PRIMARY KEY,
    recipe_id SMALLINT NOT NULL REFERENCES recipes(id),
    input_id SMALLINT NOT NULL REFERENCES inputs(id),
    quantity NUMERIC(10, 4) NOT NULL,
    UNIQUE (recipe_id, input_id)  -- Un insumo no puede repetirse en la misma receta
);

-- =============================================================
-- Tabla: products
-- Propósito: Resultado final que se vende
-- Reglas:
--   - El nombre debe ser único
--   - Siempre tiene una receta asociada
--   - price es el precio de venta manual (opcional; puede fijarse luego)
--   - target_margin: override de margen objetivo del producto (NULL = usa el global)
-- =============================================================
CREATE TABLE IF NOT EXISTS products (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price NUMERIC(10, 2),                    -- precio manual (nullable: puede no estar fijado)
    target_margin NUMERIC(5, 4),             -- override, ej 0.3500 = 35% (NULL = global)
    recipe_id SMALLINT NOT NULL REFERENCES recipes(id)
);

-- =============================================================
-- Tabla: fixed_costs
-- Propósito: Costos fijos mensuales itemizados (overhead)
-- Reglas:
--   - Cada gasto fijo se carga por separado (gas, agua, luz, alquiler...)
--   - monthly_amount es el importe mensual
-- Uso: su suma es F en COSTING_MODEL.md
-- =============================================================
CREATE TABLE IF NOT EXISTS fixed_costs (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,       -- "Gas", "Agua", "Luz", "Alquiler"
    monthly_amount NUMERIC(12, 2) NOT NULL
);

-- =============================================================
-- Tabla: employees
-- Propósito: Mano de obra mensual itemizada
-- Reglas:
--   - Cada empleado se carga por separado
--   - monthly_salary: sueldo mensual
--   - monthly_hours: horas trabajadas al mes (métrica informativa: costo por hora)
-- Uso: la suma de monthly_salary es L en COSTING_MODEL.md
-- =============================================================
CREATE TABLE IF NOT EXISTS employees (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    monthly_salary NUMERIC(12, 2) NOT NULL,
    monthly_hours NUMERIC(7, 2)              -- opcional, para métrica costo/hora
);

-- =============================================================
-- Tabla: cost_settings
-- Propósito: Parámetros globales de costeo (fila única, id = 1)
-- Reglas:
--   - default_target_margin: margen objetivo global (ej 0.3500 = 35%)
--   - monthly_material_base: base M de absorción (costo de materiales del mes típico)
--   - currency: moneda para mostrar
-- Uso: base para tasaIndirecta y precioSugerido en COSTING_MODEL.md
-- =============================================================
CREATE TABLE IF NOT EXISTS cost_settings (
    id SMALLINT NOT NULL PRIMARY KEY DEFAULT 1,
    default_target_margin NUMERIC(5, 4) NOT NULL,     -- 0.3500 = 35%
    monthly_material_base NUMERIC(12, 2) NOT NULL,    -- M
    currency VARCHAR(3) NOT NULL DEFAULT 'ARS',
    CONSTRAINT cost_settings_singleton CHECK (id = 1)
);

-- =============================================================
-- Seed: Datos iniciales
-- Unidades de medida base (corresponden al enum en Java)
-- =============================================================
INSERT INTO units_of_measurement (id, name) VALUES
    (1, 'KILOGRAM'),
    (2, 'GRAM'),
    (3, 'MILLIGRAM'),
    (4, 'LITER'),
    (5, 'MILLILITER'),
    (6, 'UNIT')
ON CONFLICT DO NOTHING;

-- Configuración de costeo por defecto (ajustar a la realidad del negocio)
INSERT INTO cost_settings (id, default_target_margin, monthly_material_base, currency) VALUES
    (1, 0.3500, 0.00, 'ARS')
ON CONFLICT DO NOTHING;
```

---

## Diccionario de entidades

### units_of_measurement
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador (1-6) |
| name | VARCHAR(50) | UNIQUE, NOT NULL | Nombre de la unidad (KILOGRAM, GRAM, etc) |

### inputs
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Nombre del insumo (Harina 000, Mantequilla, etc) |
| price | NUMERIC(10,2) | NOT NULL | Precio por unidad (ej: 1200.50) |
| unit_of_measurement_id | SMALLINT | FK, NOT NULL | Referencia a units_of_measurement |

### recipes
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Nombre de la receta (Baguette Tradicional, etc) |
| yield_quantity | NUMERIC(10,4) | NOT NULL | Cuánto rinde el lote (ej: 4 unidades, 2.5 kg) |
| yield_unit_id | SMALLINT | FK, NOT NULL | Unidad del rendimiento (KILOGRAM o UNIT) |

### ingredients
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador |
| recipe_id | SMALLINT | FK, NOT NULL | Referencia a recipes |
| input_id | SMALLINT | FK, NOT NULL | Referencia a inputs |
| quantity | NUMERIC(10,4) | NOT NULL | Cantidad (ej: 1.5 kg, 250 ml) |
| (recipe_id, input_id) | | UNIQUE | Garantiza que un insumo no se repita en la misma receta |

### products
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Nombre del producto (Baguette 250g, etc) |
| price | NUMERIC(10,2) | NULL | Precio de venta manual (opcional) |
| target_margin | NUMERIC(5,4) | NULL | Override de margen (NULL = usa el global) |
| recipe_id | SMALLINT | FK, NOT NULL | Referencia a recipes |

### fixed_costs
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador |
| name | VARCHAR(100) | UNIQUE, NOT NULL | Concepto (Gas, Agua, Luz, Alquiler) |
| monthly_amount | NUMERIC(12,2) | NOT NULL | Importe mensual |

### employees
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK | Identificador |
| name | VARCHAR(100) | NOT NULL | Nombre del empleado |
| monthly_salary | NUMERIC(12,2) | NOT NULL | Sueldo mensual |
| monthly_hours | NUMERIC(7,2) | NULL | Horas/mes (métrica costo por hora) |

### cost_settings (fila única)
| Columna | Tipo | Constraints | Descripción |
|---------|------|-------------|-------------|
| id | SMALLINT | PK, CHECK = 1 | Siempre 1 (singleton) |
| default_target_margin | NUMERIC(5,4) | NOT NULL | Margen objetivo global (0.3500 = 35%) |
| monthly_material_base | NUMERIC(12,2) | NOT NULL | Base M de absorción (materiales del mes) |
| currency | VARCHAR(3) | NOT NULL | Moneda (ARS por defecto) |

---

## Relaciones

```
units_of_measurement (1) ──< inputs (N)
units_of_measurement (1) ──< recipes (N)   [yield_unit_id]

inputs (1) ──< ingredients (N) >── (1) recipes
recipes (1) ──< products (N)

fixed_costs      (independiente, se suma para F)
employees        (independiente, se suma para L)
cost_settings    (singleton, parámetros de costeo M / margen / moneda)
```

---

## Notas importantes

1. **IDs SMALLINT**: Suficientes para un negocio pequeño a mediano (hasta 32.767).

2. **IDs asignados**: no usan AUTO_INCREMENT; se asignan desde la aplicación o por secuencias de BD.

3. **Entidad débil (ingredients)**: no existe sin una Recipe. Si se borra una Recipe debe borrarse en cascada (configurar en JPA).

4. **UNIQUE(recipe_id, input_id)**: previene el mismo insumo dos veces en una receta.

5. **Rendimiento persistido**: `recipes.yield_quantity` + `yield_unit_id` reemplazan al viejo `yieldInKilograms` que solo vivía en el dominio. Permite calcular el costo por unidad de forma persistente.

6. **Costos indirectos (fixed_costs, employees, cost_settings)**: no se asignan por FK a productos. Se **imputan por absorción** en tiempo de cálculo (ver `COSTING_MODEL.md`). La BD solo guarda los importes mensuales y los parámetros.

7. **`monthly_material_base` (M)**: es un parámetro que el negocio mantiene (costo de materiales de un mes típico). Se puede recalcular a futuro a partir del histórico de producción.

8. **Conversión de unidades (kg ↔ g, l ↔ ml)**: se maneja en lógica de aplicación, no hay tabla de conversión.

---

## Preguntas frecuentes sobre el schema

**¿Por qué `products.price` ahora es nullable?**
Porque el flujo pasa a ser: cargar receta → ver costo total → obtener precio sugerido → (opcional) fijar precio manual. Un producto puede existir sin precio fijado todavía.

**¿Por qué los costos fijos y sueldos no tienen FK a producto?**
Porque son indirectos: se reparten entre todos los productos por absorción, no pertenecen a uno solo. Ver `COSTING_MODEL.md`, sección 2.

**¿Por qué `cost_settings` es una fila única?**
Los parámetros de costeo (margen global, base de materiales, moneda) son del negocio, no por producto. El CHECK `id = 1` garantiza una sola fila.

**¿Y si en el futuro se quiere costear por hora?**
Se agrega `recipes.production_time` y se ajusta el cálculo. El schema actual no lo impide. Ver `COSTING_MODEL.md`, sección 6.
