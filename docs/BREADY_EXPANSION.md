# Bready — Expansión: autenticación, multi-tenancy, perfil, ingredientes en recetas, moneda y rebranding

**Fecha:** 2026-07-14
**Estado:** Implementado y verificado (backend 155 tests en verde; frontend build de producción OK; verificación e2e en navegador).

Este documento describe el alcance **nuevo** agregado sobre la base documentada en `COSTING_MODEL.md`, `DOMAIN_MODEL.md`, `DATABASE_SCHEMA.md` y `TICKETS.md`. El **modelo de costeo no cambió** (`COSTING_MODEL.md` sigue vigente).

---

## 1. Rebranding a "Bready"

La aplicación pasa a llamarse **Bready** ("Calculadora de costos de panadería").

- `frontend/src/index.html`: `<title>Bready</title>`.
- Shell (`core/layout/shell/`): wordmark **BREADY** (Literata + `letter-spacing`) + mascota (logo transparente) + tagline; toolbar mobile "BREADY".
- Assets en `frontend/public/assets/img/logo/` (servidos en la raíz vía el glob de `angular.json`):
  - `bready-logo.jpeg` — logo completo (mascota + wordmark + tagline). Usado en login/registro.
  - `bready-logo-recortado.jpeg` — solo la mascota (fondo crema).
  - `bready-logo-sin-fondo.png` — mascota con **fondo transparente** (usado en el sidebar).

## 2. Autenticación (JWT stateless)

Alcance **nuevo**, no previsto en el modelo de negocio original. Login + registro + logout. **La recuperación de contraseña por email queda fuera de alcance por ahora.**

- **Endpoints públicos:** `POST /api/auth/register`, `POST /api/auth/login` → `{ token, user }`.
- **Resto de `/api/**` requiere** `Authorization: Bearer <jwt>`. Sin token → **401** (formato de error uniforme).
- **Backend:** `spring-boot-starter-security` + `jjwt 0.11.5`. `infrastructure/security/`: `JwtService` (HS256, `TokenService`), `JwtAuthenticationFilter`, `SecurityConfig` (STATELESS, CORS por `CorsConfigurationSource`), `RestAuthenticationEntryPoint` (401 JSON), `SecurityCurrentUserProvider`. Password con **BCrypt**.
- **Frontend:** `core/services/auth.service.ts` (token en `localStorage`, señal `currentUser`), `core/guards/auth.guard.ts` (protege el shell), `core/interceptors/auth.interceptor.ts` (adjunta Bearer; 401 → logout). Rutas `/login` y `/register` full-page (fuera del shell). **Logout** desde el menú de usuario del shell.

## 3. Multi-tenancy (datos por usuario)

Cada usuario es **dueño de su propia panadería**: insumos, recetas, productos, costos fijos, empleados y su configuración de costeo son privados.

- **Migración** `V2__add_users_and_ownership.sql`: tabla `users`; columna `user_id NOT NULL REFERENCES users(id) ON DELETE CASCADE` en `inputs, recipes, products, fixed_costs, employees, cost_settings`; unique global de nombre → **unique compuesta `(user_id, name)`**.
- `cost_settings` deja de ser **singleton** (se quitó `CHECK (id = 1)`): ahora es **una fila por usuario** (`user_id UNIQUE`), creada automáticamente en el registro con valores por defecto (margen 0.35, base 1.00, `ARS`).
- `units_of_measurement` queda **global** (tabla de referencia).
- **Scoping** confinado a la capa de persistencia: cada `*RepositoryImpl` usa `CurrentUserProvider` para filtrar (`findByUserId`, `findByIdAndUserId`) y estampar `user_id` al guardar. Puertos, servicios y controllers **no cambian de firma** (el usuario sale del token).

## 4. Perfil de usuario

- `GET /api/profile` → `UserDTO` (`id, email, displayName, hasPhoto`; **nunca** expone el hash).
- `PUT /api/profile` → cambia el nombre visible.
- `POST /api/profile/photo` (multipart) → guarda la foto **en la base de datos** (`users.photo` `bytea` + `photo_content_type`).
- `GET /api/profile/photo` → sirve los bytes con su content-type.
- **Frontend:** `features/profile/profile-page` (editar nombre, subir/preview foto) + avatar en el sidebar (iniciales si no hay foto).

## 5. Ingredientes en recetas

El hueco reportado ("las recetas no permiten cargar ingredientes") era la **ausencia de UI de ingredientes en el alta**. Ahora:

- **Backend:** `POST /api/recipes` acepta `ingredients: [{ inputId, quantity }]` en el alta (resuelve cada insumo del usuario). Nuevo `DELETE /api/recipes/{id}/ingredients/{ingredientId}` para quitar. El alta de a uno (`POST /api/recipes/{id}/ingredients`) sigue disponible.
- **Frontend:** el diálogo de creación tiene un **`FormArray`** de filas `{ insumo, cantidad }` con agregar/quitar; el detalle de receta suma un botón **quitar** por ingrediente.

## 6. Moneda ($)

- Pipe compartido `shared/pipes/money.pipe.ts` (`money`): formatea importes como `$ 1.234,56` (locale `es-AR`), aplicado a todos los montos (dashboard, listas, detalles).
- Prefijo `$` (`matTextPrefix`) en los inputs de dinero (precio de insumo, monto de costo fijo, sueldo, precio de producto, base mensual de materiales).

---

## Evaluación del modelo de negocio (pedido: "¿lo actual cumple?")

- El backend **cumple completamente** el modelo de costeo documentado (materiales + mano de obra + costos fijos con absorción proporcional, endpoint central `GET /api/products/{id}/pricing`, 7 pantallas). No hay huecos funcionales contra `COSTING_MODEL.md`.
- **Autenticación + multi-tenancy son expansión del modelo de negocio** (de herramienta interna de una panadería a SaaS multi-panadería).
- **Extensiones futuras documentadas y no requeridas ahora** (`COSTING_MODEL.md §6`): costeo por hora (`recipes.production_time`) y simulación de punto de equilibrio. Recomendadas como trabajo futuro, no incluidas en esta entrega.

## Notas técnicas

- Build: **Spring Boot 2.7.15 / Java 11** (`javax.*`), Angular 21 standalone.
- `users.photo` se mapea como `bytea` (no `@Lob`/`oid`) para pasar `hibernate.ddl-auto=validate`.
- Tests de controllers: `@AutoConfigureMockMvc(addFilters = false)` + `@MockBean JwtService` para el slice `@WebMvcTest` bajo Spring Security.
