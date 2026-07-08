# PROMPT PARA IA - BakeryCostCalculator Backend

**Usa este prompt cada vez antes de pedirle a la IA que implemente un ticket.**

Copia el contenido de abajo y pégalo en tu conversación con la IA (OpenCode u otro).

---

## PROMPT A USAR

```
Eres un experto en arquitectura de software y desarrollo Java con Spring Boot.

Estoy desarrollando el backend de BakeryCostCalculator, una aplicación para que una panadería calcule costos de productos y fije precios.

**ANTES de generar CUALQUIER código, necesito que leas estos documentos:**

1. DOMAIN_MODEL.md - Define el modelo de dominio puro
2. ARCHITECTURE.md - Estructura de capas y flujo de requests
3. CODING_STANDARDS.md - Convenciones y estándares de código
4. DATABASE_SCHEMA.md - Schema de PostgreSQL
5. TICKETS.md - Lista de tickets de desarrollo

**Mi siguiente instrucción será:**
"Implementa TICKET-XXX"

Cuando pida un ticket, debes:
1. Buscar TICKET-XXX en TICKETS.md
2. Leer la descripción completa del ticket
3. Revisar sus dependencias (asegurate que existen)
4. Consultar DOMAIN_MODEL.md para entender el concepto
5. Consultar ARCHITECTURE.md para saber dónde va el código
6. Consultar CODING_STANDARDS.md para seguir las convenciones exactas
7. Generar SOLO lo que pide el ticket

**REGLAS CLAVE:**
- El dominio NO tiene anotaciones JPA
- Los mappers convierten manualmente entre capas
- Los servicios orquestan, los controllers reciben HTTP
- Cada clase tiene UNA sola responsabilidad
- Usa Optional en lugar de null checks
- Inyección de dependencias en constructor

¿Entendiste? Responde confirmando que estás listo.
```

---

## Cómo usarlo

1. Abre el archivo PROMPT_PARA_IA.md (este archivo)
2. Copia el contenido del prompt (entre los tres backticks)
3. Abre OpenCode o la IA que uses
4. Pega el prompt completo
5. Espera a que confirme que entiende
6. Luego dile: "Implementa TICKET-001"
7. Una vez termine, actualiza PROGRESS.md marcando el ticket como completado
8. Para el próximo ticket, copia el prompt nuevamente y pide: "Implementa TICKET-002"

---

## Qué incluir en cada prompt si necesitas aclaraciones

Si necesitas darle instrucciones adicionales a la IA para un ticket específico, agrega después del prompt base:

```
Instrucción adicional para este ticket:
[Tu instrucción aquí]
```

Ejemplo:
```
Instrucción adicional para este ticket:
- Usa @NotBlank en lugar de @NotNull para validar strings
- El método calculateCost() debe redondear a 2 decimales
```

---

## Troubleshooting

**Si la IA genera código que no sigue los estándares:**

Dile:
```
Ese código no sigue CODING_STANDARDS.md. 
Específicamente: [describe qué está mal]
Regenera respetando la sección [sección de CODING_STANDARDS.md]
```

**Si dice que no encuentra un documento:**

Asegúrate de que haya leído todos los archivos listando:
"¿Leíste DOMAIN_MODEL.md, ARCHITECTURE.md, CODING_STANDARDS.md, DATABASE_SCHEMA.md y TICKETS.md?"

Si dice que no, vuelve a compartirle los archivos o copia/pega el contenido relevante.

**Si se pierde en dependencias:**

Dile: "TICKET-XXX depende de: [listá las dependencias]. ¿Están todas implementadas?"
