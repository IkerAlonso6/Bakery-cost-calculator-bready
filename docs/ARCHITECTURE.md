# Architecture - BakeryCostCalculator

Backend en **arquitectura hexagonal (puertos y adaptadores)** con Spring Boot 3.2 / Java 17. El objetivo es aislar el dominio (reglas de negocio) de la infraestructura (BD, HTTP).

---

## Capas

```
web (adaptador de entrada)
  └─ controllers REST, exception handlers
        │  DTOs
        ▼
application (orquestación)
  └─ services, ports (interfaces de repositorio), mappers dominio↔DTO, excepciones
        │  objetos de dominio
        ▼
domain (núcleo puro)
  └─ model (Input, Recipe, Product, FixedCost, Employee, CostSettings), CostingService
        ▲
        │  implementaciones de los ports
infrastructure (adaptador de salida)
  └─ persistence: entidades JPA, JpaRepositories, RepositoryImpl, entity mappers
  └─ config: CORS, etc.
```

**Regla de dependencias:** las flechas apuntan hacia el dominio. `domain` no depende de nadie. `application` depende de `domain`. `web` e `infrastructure` dependen de `application`/`domain`. El dominio nunca importa Spring ni JPA.

---

## Estructura de paquetes (`com.bakery`)

```
com.bakery
├── BakeryCostCalculatorApplication.java
├── domain
│   └── model/            UnitOfMeasurement, Input, Ingredient, Recipe, Product,
│                         FixedCost, Employee, CostSettings, CostingService
├── application
│   ├── dto/              InputDTO, IngredientDTO, RecipeDTO, ProductDTO,
│   │                     FixedCostDTO, EmployeeDTO, CostSettingsDTO, ProductCostingDTO
│   ├── mapper/           <Entidad>Mapper (dominio ↔ DTO)
│   ├── port/             IInputRepository, IRecipeRepository, IProductRepository,
│   │                     IFixedCostRepository, IEmployeeRepository, ICostSettingsRepository
│   ├── service/          InputService, RecipeService, ProductService,
│   │                     CostingAppService (usa CostingService de dominio)
│   └── exception/        InputNotFoundException, RecipeNotFoundException, ...
├── infrastructure
│   ├── persistence
│   │   ├── entity/       *Entity (JPA)
│   │   ├── jpa/          *JpaRepository (Spring Data)
│   │   ├── mapper/       *EntityMapper (dominio ↔ entity)
│   │   └── repository/   *RepositoryImpl (implementa los ports)
│   └── config/           CorsConfig
└── web
    ├── controller/       *Controller (REST)
    └── exception/        GlobalExceptionHandler
```

---

## Flujo de un request (ejemplo: obtener costeo de un producto)

`GET /api/products/{id}/pricing`

```
1. ProductController recibe el request HTTP.
2. Llama a CostingAppService.getProductCosting(id).
3. CostingAppService:
   a. Obtiene el Product (con Recipe) vía IProductRepository (port).
      -> ProductRepositoryImpl (infra) usa ProductJpaRepository + EntityMapper
         y devuelve un objeto de DOMINIO.
   b. Obtiene FixedCosts, Employees y CostSettings vía sus ports.
   c. Invoca CostingService (dominio) con esos objetos -> ProductCosting.
4. Un mapper convierte ProductCosting -> ProductCostingDTO.
5. El controller responde 200 con el DTO (desglose + precio sugerido).
```

Puntos clave:
- El controller **no** toca JPA ni el dominio directamente para persistir: pasa por servicios y ports.
- Los repositorios (ports) hablan en objetos de **dominio**, no en entities. La conversión entity↔dominio ocurre dentro de `infrastructure`.
- Los DTOs solo cruzan la frontera `web ↔ application`. El dominio nunca ve DTOs.

---

## Puertos (ports)

Interfaces en `application/port`, implementadas en `infrastructure/persistence/repository`. Contrato base:

```java
public interface I<Entity>Repository {
    <Entity> save(<Entity> entity);
    Optional<<Entity>> findById(Integer id);
    List<<Entity>> findAll();
    void deleteById(Integer id);
}
```

Esto permite testear `application` con implementaciones falsas (in-memory) sin BD.

---

## Endpoints previstos (web)

| Recurso | Endpoints |
|---------|-----------|
| inputs | POST, GET (all), GET /{id}, PUT /{id}/price, DELETE /{id} |
| recipes | POST, GET (all), GET /{id}, POST /{id}/ingredients, GET /{id}/cost, DELETE /{id} |
| products | POST, GET (all), GET /{id}, PUT /{id}/price, PUT /{id}/margin, GET /{id}/pricing, DELETE /{id} |
| fixed-costs | POST, GET (all), GET /{id}, PUT /{id}, DELETE /{id} |
| employees | POST, GET (all), GET /{id}, PUT /{id}, DELETE /{id} |
| cost-settings | GET, PUT (fila única) |

`GET /api/products/{id}/pricing` es el endpoint central del modelo de costeo: devuelve el desglose (materiales, mano de obra, fijos, total), el precio sugerido y el margen real.

---

## Decisiones

- **Dominio sin JPA:** persistencia mediante entidades separadas + mappers. Más código, pero el dominio queda testeable y libre de framework.
- **CostingService en el dominio:** la fórmula de costeo es regla de negocio pura; no depende de BD ni HTTP.
- **CORS:** habilitado para el frontend Angular (`http://localhost:4200`) en `infrastructure/config`.
- **Migraciones SQL:** el schema (`DATABASE_SCHEMA.md`) se aplica vía `src/main/resources/db/migration`; `ddl-auto=none` (Hibernate no crea tablas).
