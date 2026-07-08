# Progress - BakeryCostCalculator

Trackea el progreso de desarrollo. Actualizá conforme completes cada ticket (ver `TICKETS.md`).

---

## Resumen

**Último update:** 2026-07-08
**Tickets completados:** 19/35
**Etapa actual:** Application Layer completado ✅ — siguiente: Infrastructure Layer (TICKET-020)

Leyenda: ⏳ Pendiente · 🔨 En progreso · ✅ Completado

---

## Documentación (base para implementar)

- [x] COSTING_MODEL.md — modelo de costeo (materiales + mano de obra + fijos, precio sugerido) ✅
- [x] DATABASE_SCHEMA.md — schema extendido (fixed_costs, employees, cost_settings, yield, margen) ✅
- [x] DOMAIN_MODEL.md ✅
- [x] ARCHITECTURE.md ✅
- [x] CODING_STANDARDS.md ✅
- [x] TICKETS.md — backlog regenerado ✅

---

## Domain Layer

- [x] TICKET-001 — Enum UnitOfMeasurement ✅ 2026-07-08
- [x] TICKET-002 — Input ✅ 2026-07-08
- [x] TICKET-003 — Ingredient ✅ 2026-07-08
- [x] TICKET-004 — Recipe (con rendimiento) ✅ 2026-07-08
- [x] TICKET-005 — Product (precio opcional + margen override) ✅ 2026-07-08
- [x] TICKET-006 — FixedCost ✅ 2026-07-08
- [x] TICKET-007 — Employee ✅ 2026-07-08
- [x] TICKET-008 — CostSettings ✅ 2026-07-08
- [x] TICKET-009 — CostingService + ProductCosting ✅ 2026-07-08 (9 tests verdes: reproduce el ejemplo de COSTING_MODEL.md)

## Application Layer

- [x] TICKET-010 — Ports de repositorio ✅ 2026-07-08
- [x] TICKET-011 — Excepciones ✅ 2026-07-08
- [x] TICKET-012 — DTOs ✅ 2026-07-08
- [x] TICKET-013 — Mappers dominio↔DTO ✅ 2026-07-08
- [x] TICKET-014 — InputService ✅ 2026-07-08
- [x] TICKET-015 — RecipeService ✅ 2026-07-08 (addIngredient resuelve Input por id)
- [x] TICKET-016 — ProductService ✅ 2026-07-08 (incluye updateTargetMargin)
- [x] TICKET-017 — FixedCostService y EmployeeService ✅ 2026-07-08 (con totales mensuales F y L)
- [x] TICKET-018 — CostSettingsService ✅ 2026-07-08
- [x] TICKET-019 — CostingAppService ✅ 2026-07-08

## Infrastructure Layer

- [ ] TICKET-020 — Migración SQL del schema (+ Flyway) ⏳
- [ ] TICKET-021 — Entidades JPA ⏳
- [ ] TICKET-022 — Entity mappers ⏳
- [ ] TICKET-023 — JpaRepositories ⏳
- [ ] TICKET-024 — RepositoryImpl ⏳
- [ ] TICKET-025 — CorsConfig ⏳

## Web Layer

- [ ] TICKET-026 — InputController ⏳
- [ ] TICKET-027 — RecipeController ⏳
- [ ] TICKET-028 — ProductController (incluye /pricing) ⏳
- [ ] TICKET-029 — FixedCostController y EmployeeController ⏳
- [ ] TICKET-030 — CostSettingsController ⏳
- [ ] TICKET-031 — GlobalExceptionHandler ⏳

## Configuration

- [ ] TICKET-032 — application.properties ⏳

## Testing (opcional, después)

- [ ] TICKET-033 — Tests de dominio ⏳
- [ ] TICKET-034 — Tests de servicios ⏳
- [ ] TICKET-035 — Tests de controllers ⏳

---

## Notas de desarrollo

### Problemas encontrados
[Documentar problemas, errores o decisiones tomadas]

### Cambios en decisiones de diseño
[Documentar desvíos del plan y su motivo]

---

## Checklist final antes de testing

- [ ] Tickets 001–032 completados
- [ ] Compila sin errores (`mvn clean install`)
- [ ] Migración Flyway aplicada (tablas + seeds, incluida cost_settings)
- [ ] DTOs con validaciones
- [ ] Servicios con manejo de excepciones
- [ ] Controllers con códigos HTTP correctos
- [ ] `GET /api/products/{id}/pricing` devuelve desglose + precio sugerido
- [ ] CostingService verificado contra el ejemplo de COSTING_MODEL.md
- [ ] CORS configurado para Angular

---

## Próximos pasos después del backend

1. Inicializar proyecto Angular (frontend/)
2. Servicio HTTP para consumir la API
3. Pantallas: Dashboard (rentabilidad), Insumos, Recetas, Productos, Costos fijos, Empleados, Configuración de costeo
4. Testing e2e
5. Deploy (Railway/Render)
