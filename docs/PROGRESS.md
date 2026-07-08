# Progress - BakeryCostCalculator

Trackea el progreso de desarrollo. Actualiza este archivo conforme completes cada ticket.

---

## Resumen de progreso

**Inicio:** [Tu fecha aquí]  
**Último update:** [Actualiza cada vez que completes un ticket]  
**Tickets completados:** [X]/30  
**Etapa actual:** [Domain Layer / Application Layer / Infrastructure Layer / Web Layer / Configuration]

---

## Domain Layer (5 tickets)

- [ ] **TICKET-001**: Crear enum UnitOfMeasurement
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-002**: Crear clase Input (dominio puro)
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-003**: Crear clase Ingredient (dominio puro)
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-004**: Crear clase Recipe (dominio puro)
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-005**: Crear clase Product (dominio puro)
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Application Layer - Interfaces y Mappers (9 tickets)

- [ ] **TICKET-006**: Crear interfaces de Repositorio (Ports)
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-007**: Crear InputMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-008**: Crear IngredientMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-009**: Crear RecipeMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-010**: Crear ProductMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-011**: Crear DTOs
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Application Layer - Servicios (4 tickets)

- [ ] **TICKET-012**: Crear InputService
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-013**: Crear RecipeService
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-014**: Crear ProductService
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-015**: Crear excepciones customizadas
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Infrastructure Layer - Entidades JPA (1 ticket)

- [ ] **TICKET-016**: Crear entidades JPA
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Infrastructure Layer - Entity Mappers (4 tickets)

- [ ] **TICKET-017**: Crear InputEntityMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-018**: Crear IngredientEntityMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-019**: Crear RecipeEntityMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-020**: Crear ProductEntityMapper
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Infrastructure Layer - Repositories (4 tickets)

- [ ] **TICKET-021**: Crear JpaRepositories
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-022**: Crear InputRepositoryImpl
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-023**: Crear RecipeRepositoryImpl
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-024**: Crear ProductRepositoryImpl
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Infrastructure Layer - Config (1 ticket)

- [ ] **TICKET-025**: Crear CorsConfig
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Web Layer - Controllers (3 tickets)

- [ ] **TICKET-026**: Crear InputController
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-027**: Crear RecipeController
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-028**: Crear ProductController
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Web Layer - Exception Handling (1 ticket)

- [ ] **TICKET-029**: Crear GlobalExceptionHandler
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Configuration (1 ticket)

- [ ] **TICKET-030**: Configurar application.properties
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Testing (Opcional, después)

- [ ] **TICKET-031**: Crear tests para dominio
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-032**: Crear tests para servicios
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

- [ ] **TICKET-033**: Crear tests para controllers
  - Status: ⏳ Pendiente
  - Completado: [fecha]
  - Notas:

---

## Notas de desarrollo

### Problemas encontrados

[Documenta problemas, errores, o decisiones tomadas aquí]

Ejemplo:
- TICKET-005: El método getMarginPercentage() necesita manejo de división por cero
- TICKET-012: El InputService requiere transacción para operaciones complejas

### Cambios en decisiones de diseño

[Si necesitas desviarte del plan original, documenta por qué aquí]

---

## Checklist final antes de testing

- [ ] Todos los tickets 001-030 completados
- [ ] Se compiló sin errores
- [ ] Se ejecutó el seed de units_of_measurement
- [ ] Los DTOs tienen validaciones
- [ ] Los servicios tienen manejo de excepciones
- [ ] Los controllers retornan códigos HTTP correctos
- [ ] CORS está configurado para Angular

---

## Próximos pasos después del backend

1. Inicializar proyecto Angular
2. Crear servicio HTTP para consumir API
3. Implementar pantallas: Dashboard, Insumos, Recetas, Productos
4. Testing e2e
5. Deploy a Railway/Render
