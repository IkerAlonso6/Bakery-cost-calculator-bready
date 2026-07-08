# Domain Model - BakeryCostCalculator

Modelo de **dominio puro** (Java sin anotaciones JPA, sin Spring). Las reglas de negocio viven acá. Las fórmulas de costeo están en `COSTING_MODEL.md`; este documento define las clases que las implementan.

> Regla de oro: el dominio no conoce base de datos, HTTP ni frameworks. Solo objetos y reglas.

---

## Panorama

```
UnitOfMeasurement (enum)
Input            -> insumo comprado (nombre, unidad, precio)
Ingredient       -> uso de un Input en una Recipe (Input + cantidad)
Recipe           -> conjunto de Ingredients + rendimiento (yield)
Product          -> Recipe + precio manual (opcional) + margen override

CostSettings     -> parámetros globales de costeo (margen global, base M, moneda)
FixedCost        -> costo fijo mensual itemizado (gas, agua, luz...)
Employee         -> mano de obra mensual itemizada (sueldo, horas)

CostingService   -> servicio de dominio que combina lo anterior y calcula
                    costo total, desglose y precio sugerido
```

---

## Entidades

### UnitOfMeasurement (enum)
Valores: `KILOGRAM(kg)`, `GRAM(g)`, `MILLIGRAM(mg)`, `LITER(l)`, `MILLILITER(ml)`, `UNIT(u)`. Cada valor tiene un símbolo.

### Input
- **Atributos:** `id (Integer)`, `name (String)`, `unitOfMeasure (UnitOfMeasurement)`, `price (BigDecimal)`
- **Constructor:** `(name, unitOfMeasure, price)`
- **Validaciones:** `name` no vacío, `price >= 0`, `unitOfMeasure` no null
- **Métodos:** `updatePrice(BigDecimal)` (valida `>= 0`); getters de solo lectura

### Ingredient
- **Atributos:** `id`, `input (Input)`, `quantity (BigDecimal)`
- **Constructor:** `(input, quantity)`
- **Validaciones:** `input` no null, `quantity > 0`
- **Métodos:** `calculateCost()` → `input.getPrice() * quantity`

### Recipe
- **Atributos:** `id`, `name (String)`, `ingredients (List<Ingredient>)`, `yieldQuantity (BigDecimal)`, `yieldUnit (UnitOfMeasurement)`
- **Constructor:** `(name, yieldQuantity, yieldUnit)`; lista de ingredientes vacía
- **Validaciones:** `name` no vacío, `yieldQuantity > 0`, `yieldUnit` no null
- **Métodos:**
  - `addIngredient(Ingredient)` (no null; no duplicar el mismo input)
  - `getIngredients()` → `Collections.unmodifiableList(...)`
  - `calculateTotalCost()` → suma de `calculateCost()` de los ingredientes (costo del lote)
  - `calculateCostPerUnit()` → `calculateTotalCost() / yieldQuantity` (**costo de materiales por unidad**)

> Cambio vs. versión previa: el rendimiento dejó de ser "en kilogramos" fijo y ahora es `yieldQuantity + yieldUnit` (kg o unidades). `calculateCostPerUnit()` reemplaza a `calculateCostPerKilogram()`.

### Product
- **Atributos:** `id`, `name (String)`, `recipe (Recipe)`, `price (BigDecimal, nullable)`, `targetMargin (BigDecimal, nullable)`
- **Constructor:** `(name, recipe)` — el precio y el margen override son opcionales
- **Validaciones:** `name` no vacío, `recipe` no null; si se setea `price`, `>= 0`; si se setea `targetMargin`, `0 <= x < 1`
- **Métodos:**
  - `updatePrice(BigDecimal)` / `updateTargetMargin(BigDecimal)` con validación
  - `getMaterialCostPerUnit()` → delega en `recipe.calculateCostPerUnit()`
  - No calcula costo total ni precio sugerido por sí solo: eso requiere los parámetros globales → lo hace `CostingService`.

### CostSettings
- **Atributos:** `defaultTargetMargin (BigDecimal)`, `monthlyMaterialBase (BigDecimal)` (= `M`), `currency (String)`
- **Validaciones:** `0 <= defaultTargetMargin < 1`, `monthlyMaterialBase > 0`, `currency` no vacío

### FixedCost
- **Atributos:** `id`, `name (String)`, `monthlyAmount (BigDecimal)`
- **Validaciones:** `name` no vacío, `monthlyAmount >= 0`

### Employee
- **Atributos:** `id`, `name (String)`, `monthlySalary (BigDecimal)`, `monthlyHours (BigDecimal, nullable)`
- **Validaciones:** `name` no vacío, `monthlySalary >= 0`, si hay `monthlyHours` → `> 0`
- **Métodos:** `costPerHour()` → `monthlySalary / monthlyHours` (métrica informativa; null si no hay horas)

---

## Servicio de dominio: CostingService

Implementa `COSTING_MODEL.md`. No es un `@Service` de Spring por sí mismo (es lógica pura); se expone a la app mediante el servicio de aplicación.

**Entradas:** un `Product` (con su `Recipe`), la lista de `FixedCost`, la lista de `Employee`, y `CostSettings`.

**Cálculos (ver fórmulas en COSTING_MODEL.md):**
```
F = Σ fixedCosts.monthlyAmount
L = Σ employees.monthlySalary
M = costSettings.monthlyMaterialBase
tasaIndirecta = (F + L) / M

materialCost = product.getMaterialCostPerUnit()
laborCost    = materialCost * (L / M)
fixedCost    = materialCost * (F / M)
totalCost    = materialCost * (1 + tasaIndirecta)

margin       = product.targetMargin != null ? product.targetMargin
                                             : costSettings.defaultTargetMargin
suggestedPrice = totalCost / (1 - margin)

realMargin   = product.price != null ? (product.price - totalCost) / product.price : null
```

**Salida:** un objeto de resultado (`ProductCosting` / DTO) con el desglose: `materialCost`, `laborCost`, `fixedCost`, `totalCost`, `suggestedPrice`, `realMargin`.

**Bordes a cuidar:**
- `M = 0` → evitar división por cero (validado en `CostSettings`, pero defender igual).
- `margin >= 1` → precio sugerido inválido (validar `< 1`).
- `product.price == null` → `realMargin = null` (aún no fijó precio).
- Redondeo: `BigDecimal` con `setScale(2, HALF_UP)` para importes monetarios.

---

## Invariantes de negocio

1. Un `Ingredient` no puede repetir `Input` dentro de la misma `Recipe`.
2. `Recipe.yieldQuantity > 0` (se divide por él).
3. `targetMargin` y `defaultTargetMargin` en `[0, 1)`.
4. Los importes monetarios nunca son negativos.
5. El costo total **siempre** incluye materiales + mano de obra + fijos (nunca solo materiales).
