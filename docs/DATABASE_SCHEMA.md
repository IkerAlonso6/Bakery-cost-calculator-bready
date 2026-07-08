# Database Schema - BakeryCostCalculator

Schema de PostgreSQL para la base de datos.

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
-- =============================================================
CREATE TABLE IF NOT EXISTS recipes (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
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
--   - El precio se asigna manualmente (no se calcula automáticamente)
--   - El precio no puede cambiar la receta del producto
-- =============================================================
CREATE TABLE IF NOT EXISTS products (
    id SMALLINT NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    price NUMERIC(10, 2) NOT NULL,
    recipe_id SMALLINT NOT NULL REFERENCES recipes(id)
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
| price | NUMERIC(10,2) | NOT NULL | Precio de venta (asignado manualmente) |
| recipe_id | SMALLINT | FK, NOT NULL | Referencia a recipes |

---

## Relaciones

```
units_of_measurement (1)
           ↑
           | (1:N)
           |
       inputs

inputs (1)
       ↑
       | (1:N)
       |
   ingredients (N)
       |
       | (N:1)
       ↓
   recipes (1)
       ↑
       | (1:N)
       |
   products (N)
```

---

## Notas importantes

1. **IDs SMALLINT**: Suficientes para un negocio pequeño a mediano. SMALLINT soporta hasta 32,767.

2. **Cascada**: Los IDs están fijos (no usan AUTO_INCREMENT). Se asignan manualmente desde la aplicación o secuencias de BD.

3. **Entidad débil (Ingredients)**: No existe sin una Recipe. Si se borra una Recipe, se borra en cascada (aunque no está explícito en el schema, debe configurarse en JPA).

4. **UNIQUE(recipe_id, input_id)**: Previene que se agregue el mismo insumo dos veces a la misma receta.

5. **Conversión de unidades**: Por ahora no hay tabla de conversión (kg ↔ g). Se maneja en lógica de aplicación.

6. **Cálculo de costos**: Se hace en memoria, no en BD. La BD solo almacena datos.

---

## Preguntas sobre el schema

**¿Por qué IDs SMALLINT en lugar de IDENTITY?**
Porque el proyecto usa IDs asignados, no autoincremento. La lógica está en la aplicación.

**¿Por qué no hay tabla de auditoría (created_at, updated_at)?**
Por ahora no es necesario. Se puede agregar después si el negocio lo requiere.

**¿Qué pasa si se borra un Input que está en uso en una Recipe?**
Actualmente la FK previene el borrado. Se puede cambiar a CASCADE si el requisito lo pide.

**¿Por qué yield no está en la tabla recipes?**
Se optó por dejarla en el modelo de dominio (Java), no en BD. Se puede agregar si es necesario persistirla.
