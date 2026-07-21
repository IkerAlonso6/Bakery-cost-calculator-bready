# Bready — Calculadora de costos de panadería

Aplicación para calcular el costo real de producción de una panadería (materiales + mano de obra + costos fijos) y sugerir un precio de venta según margen objetivo. Backend en Spring Boot (arquitectura hexagonal), frontend en Angular, autenticación JWT y datos multi-tenant (cada usuario es dueño de su propia panadería).

Ver el modelo de negocio en [`docs/COSTING_MODEL.md`](docs/COSTING_MODEL.md) y el detalle de la expansión (auth, multi-tenancy, perfil) en [`docs/BREADY_EXPANSION.md`](docs/BREADY_EXPANSION.md).

## Stack

- **Backend:** Java 11, Spring Boot 2.7.15, Spring Security (JWT), Spring Data JPA, Flyway, PostgreSQL, Maven.
- **Frontend:** Angular 21 (standalone), Angular Material, TypeScript.

## Prerequisitos

- JDK 11+
- Maven 3.9+
- PostgreSQL corriendo localmente
- Node.js 20+ y npm

## 1. Base de datos

El backend **no crea la base de datos** (solo las tablas, vía Flyway). Antes de arrancar, crear manualmente una base llamada `bakery_cost_calculator` en un Postgres escuchando en el **puerto 5433**:

```sql
CREATE DATABASE bakery_cost_calculator;
```

Credenciales por defecto (ver [`backend/src/main/resources/application.properties`](backend/src/main/resources/application.properties)): usuario `postgres`, password `postgres`. Si tu Postgres usa otro usuario/password/puerto, ajustá `spring.datasource.*` en ese archivo (o las variables de entorno equivalentes vía `SPRING_DATASOURCE_URL` / `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD`, que Spring Boot resuelve automáticamente).

Al arrancar el backend, **Flyway aplica las migraciones automáticamente** (`V1__init.sql`, `V2__add_users_and_ownership.sql`) — no hace falta correr SQL a mano.

## 2. Backend

```bash
cd backend
mvn spring-boot:run
```

Queda escuchando en `http://localhost:8080`. Endpoints bajo `/api/**`; `/api/auth/register` y `/api/auth/login` son públicos, el resto requiere `Authorization: Bearer <jwt>`.

Variables de entorno opcionales:

| Variable      | Default (dev)                                             | Uso                                    |
|---------------|-------------------------------------------------------------------|-----------------------------------------|
| `JWT_SECRET`  | secreto de desarrollo embebido (`application.properties`) | Firma de los JWT (HS256, mín. 32 bytes) |

⚠️ Para cualquier entorno que no sea desarrollo local, definir `JWT_SECRET` propio — el valor por defecto está en el repo.

## 3. Frontend

```bash
cd frontend
npm install
npm start
```

Queda escuchando en `http://localhost:4200`, apuntando al backend en `http://localhost:8080/api` (ver [`frontend/src/environments/environment.development.ts`](frontend/src/environments/environment.development.ts)). El backend tiene CORS habilitado únicamente para `http://localhost:4200` (ver [`CorsConfig.java`](backend/src/main/java/com/bakery/infrastructure/config/CorsConfig.java)).

## Tests

```bash
cd backend && mvn test   # 155 tests: dominio, servicios (Mockito), controllers (@WebMvcTest)
cd frontend && npm test  # Vitest
```

## Documentación adicional

- [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md) — capas y decisiones de arquitectura
- [`docs/DOMAIN_MODEL.md`](docs/DOMAIN_MODEL.md) — modelo de dominio
- [`docs/COSTING_MODEL.md`](docs/COSTING_MODEL.md) — fórmulas de costeo
- [`docs/DATABASE_SCHEMA.md`](docs/DATABASE_SCHEMA.md) — schema de base de datos
- [`docs/PROGRESS.md`](docs/PROGRESS.md) — estado de avance del proyecto
