# Progress - BakeryCostCalculator

Trackea el progreso de desarrollo. Actualizá conforme completes cada ticket (ver `TICKETS.md`).

---

## Resumen

**Último update:** 2026-07-08
**Tickets completados:** 0/35
**Etapa actual:** Documentación completada — pendiente inicio de implementación (Domain Layer)

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

- [ ] TICKET-001 — Enum UnitOfMeasurement ⏳
- [ ] TICKET-002 — Input ⏳
- [ ] TICKET-003 — Ingredient ⏳
- [ ] TICKET-004 — Recipe (con rendimiento) ⏳
- [ ] TICKET-005 — Product (precio opcional + margen override) ⏳
- [ ] TICKET-006 — FixedCost ⏳
- [ ] TICKET-007 — Employee ⏳
- [ ] TICKET-008 — CostSettings ⏳
- [ ] TICKET-009 — CostingService + ProductCosting ⏳

## Application Layer

- [ ] TICKET-010 — Ports de repositorio ⏳
- [ ] TICKET-011 — Excepciones ⏳
- [ ] TICKET-012 — DTOs ⏳
- [ ] TICKET-013 — Mappers dominio↔DTO ⏳
- [ ] TICKET-014 — InputService ⏳
- [ ] TICKET-015 — RecipeService ⏳
- [ ] TICKET-016 — ProductService ⏳
- [ ] TICKET-017 — FixedCostService y EmployeeService ⏳
- [ ] TICKET-018 — CostSettingsService ⏳
- [ ] TICKET-019 — CostingAppService ⏳

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
