# Coding Standards - BakeryCostCalculator

Convenciones para que todo el código sea consistente. Los tickets (`TICKETS.md`) referencian las secciones de este documento.

---

## Principios

- **Una clase, una responsabilidad.**
- **Inyección de dependencias por constructor** (no `@Autowired` en campos).
- **`Optional`** en lugar de devolver `null`.
- **Dominio puro:** sin anotaciones de Spring/JPA en `domain`.
- **`BigDecimal`** para todo importe monetario y cantidad (nunca `double`/`float`).
- Nombres en inglés para código; comentarios y docs pueden ir en español.

---

## Dominio (`domain/model`)

- Sin anotaciones de framework.
- Validaciones en el constructor; lanzar `IllegalArgumentException` con mensaje claro.
- Atributos privados; getters de solo lectura. Mutación solo por métodos con validación (`updatePrice`, etc.).
- `BigDecimal`: comparar con `.compareTo(...)`, no `==`/`equals`. Redondeo monetario `setScale(2, RoundingMode.HALF_UP)`.

```java
public Input(String name, UnitOfMeasurement unitOfMeasure, BigDecimal price) {
    if (name == null || name.isBlank()) throw new IllegalArgumentException("name required");
    if (price == null || price.signum() < 0) throw new IllegalArgumentException("price >= 0");
    if (unitOfMeasure == null) throw new IllegalArgumentException("unit required");
    // ...
}
```

---

## DTOs (`application/dto`)

- Records o clases planas con validaciones Bean Validation.
- `@NotBlank` para strings, `@NotNull` + `@Positive`/`@PositiveOrZero` para números, `@DecimalMin`/`@DecimalMax` para márgenes.

```java
public record InputDTO(
    Integer id,
    @NotBlank String name,
    @NotBlank String unitOfMeasure,
    @NotNull @Positive BigDecimal price
) {}
```

Ejemplos de validaciones por DTO:
- `IngredientDTO`: `inputId` `@NotNull`, `quantity` `@NotNull @Positive`.
- `RecipeDTO`: `name` `@NotBlank`, `yieldQuantity` `@NotNull @Positive`, `yieldUnit` `@NotBlank`.
- `ProductDTO`: `name` `@NotBlank`, `recipeId` `@NotNull`; `price` opcional `@PositiveOrZero`; `targetMargin` opcional `@DecimalMin("0.0") @DecimalMax("0.9999")`.
- `FixedCostDTO`: `name` `@NotBlank`, `monthlyAmount` `@NotNull @PositiveOrZero`.
- `EmployeeDTO`: `name` `@NotBlank`, `monthlySalary` `@NotNull @PositiveOrZero`, `monthlyHours` opcional `@Positive`.
- `CostSettingsDTO`: `defaultTargetMargin` `@NotNull @DecimalMin("0.0") @DecimalMax("0.9999")`, `monthlyMaterialBase` `@NotNull @Positive`, `currency` `@NotBlank`.

---

## Mappers (`application/mapper` y `infrastructure/persistence/mapper`)

- Conversión **manual** (sin MapStruct), métodos estáticos o componentes `@Component`.
- `application/mapper`: dominio ↔ DTO.
- `infrastructure/persistence/mapper`: dominio ↔ entity.
- Un mapper por entidad. Métodos: `toDomain(...)`, `toDto(...)` / `toEntity(...)`, y versión para listas.

---

## Ports y RepositoryImpl

- Port (interface) en `application/port`; habla en objetos de **dominio**.
- `RepositoryImpl` en `infrastructure/persistence/repository`; usa el `JpaRepository` + `EntityMapper` y convierte en cada operación.

```java
@Repository
public class InputRepositoryImpl implements IInputRepository {
    private final InputJpaRepository jpa;
    private final InputEntityMapper mapper;
    // constructor injection
    public Input save(Input input) {
        return mapper.toDomain(jpa.save(mapper.toEntity(input)));
    }
}
```

---

## Entidades JPA (`infrastructure/persistence/entity`)

- `@Entity`, `@Table(name = "...")`, `@Id`. Constructor vacío (requerido por JPA).
- Relaciones `@ManyToOne`/`@OneToMany` con `fetch = LAZY`.
- Getters/setters públicos (a diferencia del dominio).
- Mapear exactamente a las tablas de `DATABASE_SCHEMA.md`.

---

## Servicios (`application/service`)

- Anotados `@Service`; dependencias por constructor.
- Orquestan: obtienen del repositorio, aplican dominio, persisten.
- Lanzan excepciones de `application/exception` cuando no encuentran (`...NotFoundException`).
- `CostingAppService` reúne Product + FixedCosts + Employees + CostSettings e invoca el `CostingService` de dominio.

---

## Manejo de excepciones

- Excepciones de dominio/aplicación extienden `RuntimeException` con mensajes claros (`InputNotFoundException`, `RecipeNotFoundException`, `ProductNotFoundException`).
- `GlobalExceptionHandler` (`@RestControllerAdvice`) mapea:
  - `*NotFoundException` → 404
  - `MethodArgumentNotValidException` / `IllegalArgumentException` → 400
  - genéricas → 500
- Respuesta de error uniforme: `{ "timestamp", "status", "error", "message" }`.

---

## Controllers (`web/controller`)

- `@RestController`, `@RequestMapping("/api/...")`.
- Reciben/retornan **DTOs**, nunca dominio ni entities.
- Códigos HTTP: `201 Created`, `200 OK`, `204 No Content`, `400`, `404`.
- Validar el body con `@Valid`.

---

## Tests

- Dominio: unit tests puros (sin Spring) — foco en validaciones y cálculos (`CostingService` con el ejemplo de `COSTING_MODEL.md`).
- Servicios: mock de los ports.
- Controllers: `@WebMvcTest` + MockMvc.
