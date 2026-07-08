# Development Tickets - BakeryCostCalculator

Tickets organizados por capa, en orden de implementación. Cada ticket es autocontendido y puede ejecutarse independientemente una vez completados sus dependencias.

---

## Domain Layer Tickets

### TICKET-001: Crear enum UnitOfMeasurement
**Dependencias:** Ninguna  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/domain/model/UnitOfMeasurement.java`

**Descripción:**
Crear el enum UnitOfMeasurement con los valores: KILOGRAM, GRAM, MILLIGRAM, LITER, MILLILITER, UNIT.
Cada valor debe tener un símbolo asociado (kg, g, mg, l, ml, u).

**Validaciones:** Ninguna (es un enum)

---

### TICKET-002: Crear clase Input (dominio puro)
**Dependencias:** TICKET-001  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/domain/model/Input.java`

**Descripción:**
Crear la clase Input con:
- Atributos: id (Integer), name (String), unitOfMeasure (UnitOfMeasurement), price (BigDecimal)
- Constructor con parámetros (name, unitOfMeasure, price)
- Validaciones en constructor: name no vacío, price >= 0, unitOfMeasure no null
- Getters privados para id, name, unitOfMeasure (solo lectura)
- Método updatePrice(BigDecimal) con validación
- toString()

Referencia: DOMAIN_MODEL.md - sección Input

---

### TICKET-003: Crear clase Ingredient (dominio puro)
**Dependencias:** TICKET-002  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/domain/model/Ingredient.java`

**Descripción:**
Crear la clase Ingredient con:
- Atributos: id (Integer), input (Input), quantity (BigDecimal)
- Constructor con parámetros (input, quantity)
- Validaciones: quantity > 0, input no null
- Método calculateCost() que retorna input.getPrice() * quantity
- Getters para todos los atributos (solo lectura)
- toString()

Referencia: DOMAIN_MODEL.md - sección Ingredient

---

### TICKET-004: Crear clase Recipe (dominio puro)
**Dependencias:** TICKET-003  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/domain/model/Recipe.java`

**Descripción:**
Crear la clase Recipe con:
- Atributos: id (Integer), ingredients (List<Ingredient> privada), yieldInKilograms (BigDecimal)
- Constructor sin parámetros (ingredients = lista vacía)
- Método addIngredient(Ingredient) con validación (no null)
- Método getIngredients() que retorna Collections.unmodifiableList()
- Método calculateTotalCost() que suma costos de ingredientes
- Método calculateCostPerKilogram() que retorna totalCost / yieldInKilograms
- Setters solo para yieldInKilograms (con validación > 0)
- toString()

Referencia: DOMAIN_MODEL.md - sección Recipe

---

### TICKET-005: Crear clase Product (dominio puro)
**Dependencias:** TICKET-004  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/domain/model/Product.java`

**Descripción:**
Crear la clase Product con:
- Atributos: id (Integer), name (String), recipe (Recipe), price (BigDecimal)
- Constructor con parámetros (name, recipe, price)
- Validaciones en constructor: name no vacío, recipe no null, price >= 0
- Getters para todos (solo lectura)
- Método updatePrice(BigDecimal) con validación
- Método getRecipeCost() que delega en recipe.calculateCostPerKilogram()
- Método getMarginPercentage() que calcula ((price - recipeCost) / recipeCost) * 100
- toString()

Referencia: DOMAIN_MODEL.md - sección Product

---

## Application Layer Tickets

### TICKET-006: Crear interfaces de Repositorio (Ports)
**Dependencias:** TICKET-001, TICKET-002, TICKET-004, TICKET-005  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/port/IInputRepository.java`
- `backend/src/main/java/com/bakery/application/port/IRecipeRepository.java`
- `backend/src/main/java/com/bakery/application/port/IProductRepository.java`

**Descripción:**
Crear las interfaces de repositorio con métodos:
- save(T entity) -> T
- findById(Integer id) -> Optional<T>
- findAll() -> List<T>
- deleteById(Integer id) -> void

Referencia: ARCHITECTURE.md - sección Port

---

### TICKET-007: Crear InputMapper
**Dependencias:** TICKET-002, TICKET-006  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/mapper/InputMapper.java`

**Descripción:**
Crear mapper que convierte:
- InputDTO -> Input (dominio)
- Input -> InputDTO
- List<Input> -> List<InputDTO>

Referencia: CODING_STANDARDS.md - sección Mappers

---

### TICKET-008: Crear IngredientMapper
**Dependencias:** TICKET-003, TICKET-007  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/mapper/IngredientMapper.java`

**Descripción:**
Crear mapper que convierte:
- IngredientDTO -> Ingredient (dominio)
- Ingredient -> IngredientDTO
- List<Ingredient> -> List<IngredientDTO>

Referencia: CODING_STANDARDS.md - sección Mappers

---

### TICKET-009: Crear RecipeMapper
**Dependencias:** TICKET-004, TICKET-008  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/mapper/RecipeMapper.java`

**Descripción:**
Crear mapper que convierte:
- RecipeDTO -> Recipe (dominio)
- Recipe -> RecipeDTO (incluir ingredientes)
- List<Recipe> -> List<RecipeDTO>

Referencia: CODING_STANDARDS.md - sección Mappers

---

### TICKET-010: Crear ProductMapper
**Dependencias:** TICKET-005, TICKET-009  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/mapper/ProductMapper.java`

**Descripción:**
Crear mapper que convierte:
- ProductDTO -> Product (dominio)
- Product -> ProductDTO
- List<Product> -> List<ProductDTO>

Referencia: CODING_STANDARDS.md - sección Mappers

---

### TICKET-011: Crear DTOs
**Dependencias:** TICKET-007, TICKET-008, TICKET-009, TICKET-010  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/dto/InputDTO.java`
- `backend/src/main/java/com/bakery/application/dto/IngredientDTO.java`
- `backend/src/main/java/com/bakery/application/dto/RecipeDTO.java`
- `backend/src/main/java/com/bakery/application/dto/ProductDTO.java`

**Descripción:**
Crear DTOs con validaciones:
- InputDTO: name (@NotBlank), unitOfMeasure (@NotBlank), price (@NotNull, @Positive)
- IngredientDTO: inputId (@NotNull), quantity (@NotNull, @Positive)
- RecipeDTO: name (@NotBlank), ingredients (List)
- ProductDTO: name (@NotBlank), recipeId (@NotNull), price (@NotNull, @Positive)

Referencia: CODING_STANDARDS.md - sección DTOs

---

### TICKET-012: Crear InputService
**Dependencias:** TICKET-002, TICKET-006, TICKET-007  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/service/InputService.java`

**Descripción:**
Crear servicio con métodos:
- createInput(String name, UnitOfMeasurement unit, BigDecimal price) -> Input
- getInputById(Integer id) -> Input (lanza InputNotFoundException si no existe)
- getAllInputs() -> List<Input>
- updateInputPrice(Integer id, BigDecimal newPrice) -> Input
- deleteInput(Integer id) -> void

Referencia: CODING_STANDARDS.md - sección Servicios

---

### TICKET-013: Crear RecipeService
**Dependencias:** TICKET-004, TICKET-006, TICKET-009  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/service/RecipeService.java`

**Descripción:**
Crear servicio con métodos:
- createRecipe(String name) -> Recipe
- getRecipeById(Integer id) -> Recipe (lanza RecipeNotFoundException si no existe)
- getAllRecipes() -> List<Recipe>
- addIngredientToRecipe(Integer recipeId, Ingredient ingredient) -> Recipe
- calculateRecipeCost(Integer recipeId) -> BigDecimal
- deleteRecipe(Integer id) -> void

Referencia: CODING_STANDARDS.md - sección Servicios

---

### TICKET-014: Crear ProductService
**Dependencias:** TICKET-005, TICKET-006, TICKET-010  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/service/ProductService.java`

**Descripción:**
Crear servicio con métodos:
- createProduct(String name, Integer recipeId, BigDecimal price) -> Product
- getProductById(Integer id) -> Product (lanza ProductNotFoundException si no existe)
- getAllProducts() -> List<Product>
- updateProductPrice(Integer id, BigDecimal newPrice) -> Product
- getProductMargin(Integer id) -> BigDecimal
- deleteProduct(Integer id) -> void

Referencia: CODING_STANDARDS.md - sección Servicios

---

### TICKET-015: Crear excepciones customizadas
**Dependencias:** Ninguna  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/application/exception/InputNotFoundException.java`
- `backend/src/main/java/com/bakery/application/exception/RecipeNotFoundException.java`
- `backend/src/main/java/com/bakery/application/exception/ProductNotFoundException.java`

**Descripción:**
Crear excepciones que extienden RuntimeException con mensajes claros.

Referencia: CODING_STANDARDS.md - sección Manejo de excepciones

---

## Infrastructure Layer Tickets

### TICKET-016: Crear entidades JPA
**Dependencias:** TICKET-001  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/entity/UnitOfMeasurementEntity.java`
- `backend/src/main/java/com/bakery/infrastructure/persistence/entity/InputEntity.java`
- `backend/src/main/java/com/bakery/infrastructure/persistence/entity/IngredientEntity.java`
- `backend/src/main/java/com/bakery/infrastructure/persistence/entity/RecipeEntity.java`
- `backend/src/main/java/com/bakery/infrastructure/persistence/entity/ProductEntity.java`

**Descripción:**
Crear entidades JPA con:
- Anotaciones @Entity y @Table
- @Id con @GeneratedValue(IDENTITY)
- @Column con constraints (nullable, unique, length)
- Relaciones: @OneToMany, @ManyToOne con fetch = LAZY
- Constructor vacío (requerido por JPA)
- Getters y setters públicos

Mapeo con tablas de schema.sql:
- UnitOfMeasurementEntity -> units_of_measurement
- InputEntity -> inputs
- RecipeEntity -> recipes
- IngredientEntity -> ingredients
- ProductEntity -> products

Referencia: CODING_STANDARDS.md - sección Entidades JPA

---

### TICKET-017: Crear InputEntityMapper
**Dependencias:** TICKET-002, TICKET-016  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/mapper/InputEntityMapper.java`

**Descripción:**
Crear mapper que convierte:
- Input (dominio) -> InputEntity (para guardar)
- InputEntity -> Input (dominio) (al recuperar)

Referencia: CODING_STANDARDS.md - sección EntityMapper

---

### TICKET-018: Crear IngredientEntityMapper
**Dependencias:** TICKET-003, TICKET-016, TICKET-017  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/mapper/IngredientEntityMapper.java`

**Descripción:**
Crear mapper que convierte:
- Ingredient (dominio) -> IngredientEntity
- IngredientEntity -> Ingredient (dominio)

---

### TICKET-019: Crear RecipeEntityMapper
**Dependencias:** TICKET-004, TICKET-016, TICKET-018  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/mapper/RecipeEntityMapper.java`

**Descripción:**
Crear mapper que convierte:
- Recipe (dominio) -> RecipeEntity
- RecipeEntity -> Recipe (dominio)

Nota: Manejar la lista de ingredientes correctamente.

---

### TICKET-020: Crear ProductEntityMapper
**Dependencias:** TICKET-005, TICKET-016, TICKET-019  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/mapper/ProductEntityMapper.java`

**Descripción:**
Crear mapper que convierte:
- Product (dominio) -> ProductEntity
- ProductEntity -> Product (dominio)

---

### TICKET-021: Crear JpaRepositories
**Dependencias:** TICKET-016  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/jpa/InputJpaRepository.java`
- `backend/src/main/java/com/bakery/infrastructure/persistence/jpa/RecipeJpaRepository.java`
- `backend/src/main/java/com/bakery/infrastructure/persistence/jpa/ProductJpaRepository.java`

**Descripción:**
Crear interfaces que extienden JpaRepository<Entity, Integer>.
Métodos automáticos: findAll(), save(), findById(), deleteById()
Métodos customizados: findByName() donde sea aplicable.

Referencia: CODING_STANDARDS.md - sección JpaRepository

---

### TICKET-022: Crear InputRepositoryImpl
**Dependencias:** TICKET-006, TICKET-017, TICKET-021  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/repository/InputRepositoryImpl.java`

**Descripción:**
Implementar IInputRepository usando InputJpaRepository e InputEntityMapper.
Convertir Entity ↔ Dominio en cada operación.

Referencia: CODING_STANDARDS.md - sección RepositoryImpl

---

### TICKET-023: Crear RecipeRepositoryImpl
**Dependencias:** TICKET-006, TICKET-019, TICKET-021  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/repository/RecipeRepositoryImpl.java`

**Descripción:**
Implementar IRecipeRepository usando RecipeJpaRepository e RecipeEntityMapper.

---

### TICKET-024: Crear ProductRepositoryImpl
**Dependencias:** TICKET-006, TICKET-020, TICKET-021  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/persistence/repository/ProductRepositoryImpl.java`

**Descripción:**
Implementar IProductRepository usando ProductJpaRepository e ProductEntityMapper.

---

### TICKET-025: Crear CorsConfig
**Dependencias:** Ninguna  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/infrastructure/config/CorsConfig.java`

**Descripción:**
Crear configuración CORS para permitir requests desde Angular (http://localhost:4200).

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:4200")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

---

## Web Layer Tickets

### TICKET-026: Crear InputController
**Dependencias:** TICKET-012, TICKET-007  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/web/controller/InputController.java`

**Descripción:**
Crear REST controller con endpoints:
- POST /api/inputs (crear)
- GET /api/inputs (listar todos)
- GET /api/inputs/{id} (obtener uno)
- PUT /api/inputs/{id}/price (actualizar precio)
- DELETE /api/inputs/{id} (eliminar)

Códigos HTTP: 201 CREATE, 200 OK, 204 NO CONTENT, 404 NOT FOUND

Referencia: CODING_STANDARDS.md - sección Controllers

---

### TICKET-027: Crear RecipeController
**Dependencias:** TICKET-013, TICKET-009  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/web/controller/RecipeController.java`

**Descripción:**
Crear REST controller con endpoints:
- POST /api/recipes (crear)
- GET /api/recipes (listar todas)
- GET /api/recipes/{id} (obtener una)
- POST /api/recipes/{id}/ingredients (agregar ingrediente)
- GET /api/recipes/{id}/cost (calcular costo)
- DELETE /api/recipes/{id} (eliminar)

---

### TICKET-028: Crear ProductController
**Dependencias:** TICKET-014, TICKET-010  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/web/controller/ProductController.java`

**Descripción:**
Crear REST controller con endpoints:
- POST /api/products (crear)
- GET /api/products (listar todos)
- GET /api/products/{id} (obtener uno)
- PUT /api/products/{id}/price (actualizar precio)
- GET /api/products/{id}/margin (obtener margen)
- DELETE /api/products/{id} (eliminar)

---

### TICKET-029: Crear GlobalExceptionHandler
**Dependencias:** TICKET-015  
**Archivos a generar:**
- `backend/src/main/java/com/bakery/web/exception/GlobalExceptionHandler.java`

**Descripción:**
Crear manejador global de excepciones con handlers para:
- InputNotFoundException, RecipeNotFoundException, ProductNotFoundException (404)
- MethodArgumentNotValidException (400)
- Excepciones genéricas (500)

Referencia: CODING_STANDARDS.md - sección GlobalExceptionHandler

---

## Configuration Tickets

### TICKET-030: Configurar application.properties
**Dependencias:** Ninguna  
**Archivos a generar/modificar:**
- `backend/src/main/resources/application.properties`

**Descripción:**
Configurar:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bakery_cost_calculator
spring.datasource.username=postgres
spring.datasource.password=tu_password
spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
server.port=8080
```

---

## Testing Tickets (Opcionales, para después)

### TICKET-031: Crear tests para dominio
### TICKET-032: Crear tests para servicios
### TICKET-033: Crear tests para controllers

---

## Orden de implementación recomendado

1. Dominio: TICKET-001 → TICKET-005
2. Application (mappers y DTOs): TICKET-007 → TICKET-011
3. Application (services): TICKET-012 → TICKET-014
4. Excepciones: TICKET-015
5. Infrastructure (entidades y mappers): TICKET-016 → TICKET-020
6. Infrastructure (repositories): TICKET-021 → TICKET-024
7. Infrastructure (config): TICKET-025
8. Configuration: TICKET-030
9. Web: TICKET-026 → TICKET-028
10. Exception handling: TICKET-029

---

## Notas

- Cada ticket depende de los anteriores. No saltar tickets.
- Si un ticket falla, revisar las dependencias.
- Los tests pueden hacerse después de completar todos los tickets funcionales.
