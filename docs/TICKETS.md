# Development Tickets - BakeryCostCalculator

Backlog por capas, en orden de implementación. Cada ticket es autocontenido una vez completadas sus dependencias.

**Antes de implementar, leer:** `COSTING_MODEL.md`, `DOMAIN_MODEL.md`, `ARCHITECTURE.md`, `CODING_STANDARDS.md`, `DATABASE_SCHEMA.md`.

Convención de rutas: paquete base `backend/src/main/java/com/bakery/`.

---

## Domain Layer

### TICKET-001: Enum UnitOfMeasurement
**Dep:** ninguna · **Archivo:** `domain/model/UnitOfMeasurement.java`
Valores KILOGRAM(kg), GRAM(g), MILLIGRAM(mg), LITER(l), MILLILITER(ml), UNIT(u), cada uno con su símbolo.

### TICKET-002: Clase Input
**Dep:** 001 · **Archivo:** `domain/model/Input.java`
Atributos `id, name, unitOfMeasure, price`. Constructor `(name, unitOfMeasure, price)` con validaciones (name no vacío, price ≥ 0, unit no null). `updatePrice()`. Ref: DOMAIN_MODEL.md · Input.

### TICKET-003: Clase Ingredient
**Dep:** 002 · **Archivo:** `domain/model/Ingredient.java`
Atributos `id, input, quantity`. Validaciones (input no null, quantity > 0). `calculateCost()` = price × quantity.

### TICKET-004: Clase Recipe (con rendimiento)
**Dep:** 003 · **Archivo:** `domain/model/Recipe.java`
Atributos `id, name, ingredients (List), yieldQuantity, yieldUnit`. Constructor `(name, yieldQuantity, yieldUnit)`. `addIngredient()` (no duplicar input), `getIngredients()` inmodificable, `calculateTotalCost()` (costo del lote), **`calculateCostPerUnit()` = totalCost / yieldQuantity**. Ref: DOMAIN_MODEL.md · Recipe.

### TICKET-005: Clase Product (precio opcional + margen override)
**Dep:** 004 · **Archivo:** `domain/model/Product.java`
Atributos `id, name, recipe, price (nullable), targetMargin (nullable)`. Constructor `(name, recipe)`. `updatePrice()`, `updateTargetMargin()` (0 ≤ x < 1), `getMaterialCostPerUnit()` (delega en recipe). **No** calcula total ni precio sugerido (eso es CostingService).

### TICKET-006: Clase FixedCost
**Dep:** ninguna · **Archivo:** `domain/model/FixedCost.java`
Atributos `id, name, monthlyAmount`. Validaciones (name no vacío, monthlyAmount ≥ 0).

### TICKET-007: Clase Employee
**Dep:** ninguna · **Archivo:** `domain/model/Employee.java`
Atributos `id, name, monthlySalary, monthlyHours (nullable)`. `costPerHour()` (métrica; null si sin horas).

### TICKET-008: Clase CostSettings
**Dep:** ninguna · **Archivo:** `domain/model/CostSettings.java`
Atributos `defaultTargetMargin, monthlyMaterialBase (M), currency`. Validaciones (margen en [0,1), M > 0).

### TICKET-009: CostingService (dominio) + resultado ProductCosting
**Dep:** 005, 006, 007, 008 · **Archivos:** `domain/model/CostingService.java`, `domain/model/ProductCosting.java`
Implementa las fórmulas de COSTING_MODEL.md: `tasaIndirecta = (F+L)/M`; desglose `materialCost, laborCost, fixedCost, totalCost`; `suggestedPrice = totalCost/(1-margin)`; `realMargin`. Cuidar división por cero, margen ≥ 1 y redondeo HALF_UP. **Test obligatorio:** reproducir el ejemplo numérico del COSTING_MODEL.md.

---

## Application Layer

### TICKET-010: Ports de repositorio
**Dep:** 002,004,005,006,007,008 · **Archivos:** `application/port/I{Input,Recipe,Product,FixedCost,Employee,CostSettings}Repository.java`
Métodos base: `save`, `findById→Optional`, `findAll`, `deleteById`. CostSettings expone `get()`/`save()` (singleton).

### TICKET-011: Excepciones
**Dep:** ninguna · **Archivos:** `application/exception/{Input,Recipe,Product,FixedCost,Employee}NotFoundException.java`
Extienden RuntimeException con mensaje claro.

### TICKET-012: DTOs
**Dep:** ninguna · **Archivos:** `application/dto/*DTO.java`
`InputDTO, IngredientDTO, RecipeDTO, ProductDTO, FixedCostDTO, EmployeeDTO, CostSettingsDTO, ProductCostingDTO`. Validaciones según CODING_STANDARDS.md · DTOs (incluye targetMargin opcional, yield en RecipeDTO, desglose en ProductCostingDTO).

### TICKET-013: Mappers dominio↔DTO
**Dep:** 010, 012 · **Archivos:** `application/mapper/*Mapper.java`
Uno por entidad. `toDomain`, `toDto`, versión lista. Incluir `ProductCostingMapper` (ProductCosting → ProductCostingDTO).

### TICKET-014: InputService
**Dep:** 010, 011, 013 · `application/service/InputService.java`
create, getById (404), getAll, updatePrice, delete.

### TICKET-015: RecipeService
**Dep:** 010, 011, 013 · `application/service/RecipeService.java`
create (con yield), getById (404), getAll, addIngredient, calculateCost (costo por unidad), delete.

### TICKET-016: ProductService
**Dep:** 010, 011, 013 · `application/service/ProductService.java`
create, getById (404), getAll, updatePrice, updateMargin, delete.

### TICKET-017: FixedCostService y EmployeeService
**Dep:** 010, 011, 013 · `application/service/{FixedCostService,EmployeeService}.java`
CRUD estándar de costos fijos y empleados.

### TICKET-018: CostSettingsService
**Dep:** 010, 013 · `application/service/CostSettingsService.java`
get() y update() de la fila única.

### TICKET-019: CostingAppService
**Dep:** 009, 016, 017, 018 · `application/service/CostingAppService.java`
`getProductCosting(id)`: reúne Product + FixedCosts + Employees + CostSettings, invoca `CostingService` y devuelve `ProductCosting`. Endpoint central del sistema.

---

## Infrastructure Layer

### TICKET-020: Migración SQL del schema
**Dep:** ninguna · **Archivo:** `src/main/resources/db/migration/V1__init.sql`
Todo el DDL + seeds de DATABASE_SCHEMA.md (incluye fixed_costs, employees, cost_settings, yield en recipes, target_margin en products). Agregar dependencia Flyway al `pom.xml`.

### TICKET-021: Entidades JPA
**Dep:** 020 · **Archivos:** `infrastructure/persistence/entity/*Entity.java`
`UnitOfMeasurementEntity, InputEntity, IngredientEntity, RecipeEntity, ProductEntity, FixedCostEntity, EmployeeEntity, CostSettingsEntity`. Mapear exactamente a las tablas. Relaciones LAZY, constructor vacío.

### TICKET-022: Entity mappers (dominio↔entity)
**Dep:** 021 · **Archivos:** `infrastructure/persistence/mapper/*EntityMapper.java`
Uno por entidad. Cuidar la lista de ingredientes en Recipe.

### TICKET-023: JpaRepositories
**Dep:** 021 · **Archivos:** `infrastructure/persistence/jpa/*JpaRepository.java`
Extienden `JpaRepository<Entity, Integer>`; `findByName` donde aplique.

### TICKET-024: RepositoryImpl
**Dep:** 010, 022, 023 · **Archivos:** `infrastructure/persistence/repository/*RepositoryImpl.java`
Implementan los ports usando JpaRepository + EntityMapper. Incluye CostSettings (singleton).

### TICKET-025: CorsConfig
**Dep:** ninguna · **Archivo:** `infrastructure/config/CorsConfig.java`
Permitir `http://localhost:4200`, métodos GET/POST/PUT/DELETE.

---

## Web Layer

### TICKET-026: InputController
**Dep:** 014 · `web/controller/InputController.java`
POST, GET, GET/{id}, PUT/{id}/price, DELETE/{id}. Códigos 201/200/204/404.

### TICKET-027: RecipeController
**Dep:** 015 · `web/controller/RecipeController.java`
POST, GET, GET/{id}, POST/{id}/ingredients, GET/{id}/cost, DELETE/{id}.

### TICKET-028: ProductController (incluye pricing)
**Dep:** 016, 019 · `web/controller/ProductController.java`
POST, GET, GET/{id}, PUT/{id}/price, PUT/{id}/margin, **GET/{id}/pricing** (desglose + precio sugerido + margen real), DELETE/{id}.

### TICKET-029: FixedCostController y EmployeeController
**Dep:** 017 · `web/controller/{FixedCostController,EmployeeController}.java`
CRUD REST de cada uno.

### TICKET-030: CostSettingsController
**Dep:** 018 · `web/controller/CostSettingsController.java`
GET y PUT de la configuración de costeo.

### TICKET-031: GlobalExceptionHandler
**Dep:** 011 · `web/exception/GlobalExceptionHandler.java`
`*NotFoundException`→404, validación/`IllegalArgumentException`→400, genéricas→500. Respuesta uniforme.

---

## Configuration

### TICKET-032: application.properties
**Dep:** 020 · `src/main/resources/application.properties`
Datasource PostgreSQL, `ddl-auto=none`, Flyway habilitado, `server.port=8080`. Documentar la variable de password.

---

## Testing (después del backend funcional)

### TICKET-033: Tests de dominio
Foco en validaciones y en `CostingService` (reproducir ejemplo de COSTING_MODEL.md).
### TICKET-034: Tests de servicios (mock de ports)
### TICKET-035: Tests de controllers (`@WebMvcTest` + MockMvc)

---

## Orden recomendado

1. Dominio: 001 → 009
2. Application: ports/excepciones/DTOs/mappers (010–013) → servicios (014–019)
3. Infra: migración (020) → entidades/mappers/repos (021–024) → CORS (025)
4. Config: 032
5. Web: controllers (026–030) → exception handler (031)
6. Testing: 033 → 035

**Nota:** no saltar dependencias. Si un ticket falla, revisar sus dependencias.
