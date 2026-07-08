# Modelo de Costeo — BakeryCostCalculator

Este documento define **cómo se calcula el costo real de un producto** y **cómo se sugiere su precio de venta**. Es la referencia funcional central del sistema: el dominio (`DOMAIN_MODEL.md`), el schema (`DATABASE_SCHEMA.md`) y los tickets (`TICKETS.md`) derivan de acá.

---

## 1. Problema de negocio

Una panadería no es rentable solo cubriendo la **materia prima**. El costo real de cada producto incluye tres componentes:

| Componente | Ejemplos | Naturaleza |
|-----------|----------|-----------|
| **Costo directo** (materiales) | Harina, manteca, huevos, levadura | Varía con lo que se produce; ya modelado (insumos → ingredientes → receta) |
| **Mano de obra** | Sueldos de panaderos, ayudantes | Fijo mensual, pero se consume al producir |
| **Costos fijos / overhead** | Gas, agua, luz, alquiler | Fijo mensual, independiente del volumen |

El modelo anterior solo consideraba el costo directo. Este documento incorpora **mano de obra** y **costos fijos** para obtener el **costo total** y, a partir de un **margen objetivo**, **sugerir un precio de venta** que maximice la ganancia sin quedar por debajo del punto de rentabilidad.

---

## 2. Decisión de diseño: cómo se imputan los costos indirectos

Mano de obra y costos fijos son **costos indirectos**: no se pueden asignar a un producto de forma directa como un ingrediente. Hay que **repartirlos** (imputarlos) según algún criterio (*driver de asignación*).

### Restricciones del negocio (definidas con el usuario)
1. Los costos fijos y los sueldos se cargan **detallados por ítem** y son **totales mensuales**.
2. **No se registra tiempo de producción por receta** (no se cronometra).
3. Las recetas rinden en **kg o unidades**.

### Consecuencia
Sin tiempo por receta, **no es posible imputar costos "por hora" a cada producto** (no sabemos cuántas horas consume cada receta). Tampoco conviene repartir por volumen físico directo, porque unos productos se miden en kg y otros en unidades (no son sumables entre sí).

### Método elegido: **absorción proporcional al costo directo**
Cada producto absorbe costos indirectos **en proporción a su costo de materiales**. Es un método de costeo por absorción estándar, y para este negocio es el mejor equilibrio entre precisión y datos disponibles:

- No requiere cronometrar recetas.
- No requiere convertir kg ↔ unidades.
- Usa los **totales mensuales reales** de costos fijos y sueldos.
- Convierte un "porcentaje de recargo" adivinado en un **porcentaje derivado de datos reales**.

> **Nota:** este método asume que los productos más caros en materiales tienden a consumir más recursos indirectos. Es una aproximación razonable para una panadería. En la Sección 6 se documentan alternativas por si el negocio evoluciona (p. ej., si en el futuro se decide registrar tiempos).

---

## 3. Fórmulas

### 3.1 Parámetros mensuales del negocio
Se configuran una vez y se actualizan cuando cambian (tabla `cost_settings` + `fixed_costs` + `employees`):

```
F = Σ costos fijos del mes          (suma de fixed_costs.monthly_amount)
L = Σ sueldos del mes               (suma de employees.monthly_salary)
M = base mensual de materiales      (costo de materiales de la producción esperada del mes)
```

`M` es la base de absorción: representa cuánto costo de materiales genera el negocio en un mes típico. Se configura en `cost_settings.monthly_material_base`.

### 3.2 Tasa de indirectos (overhead rate)
```
tasaIndirecta = (F + L) / M
```
Interpretación: por cada $1 de materiales, el negocio suma `tasaIndirecta` de costos indirectos (mano de obra + fijos).

### 3.3 Costo total de un producto
```
costoMateriales = costo de la receta por unidad
                = (Σ ingredientes de la receta) / rendimiento
costoIndirecto  = costoMateriales × tasaIndirecta
costoTotal      = costoMateriales × (1 + tasaIndirecta)
```

Desglose que la app debe mostrar por producto: `costoMateriales`, `costoManoObra`, `costoFijo`, `costoTotal`. La mano de obra y el fijo se separan aplicando su peso relativo:
```
costoManoObra = costoMateriales × (L / M)
costoFijo     = costoMateriales × (F / M)
```

### 3.4 Margen objetivo y precio sugerido
El **margen** se define como **porcentaje sobre el precio de venta** (margen de contribución), no sobre el costo. Es el criterio correcto para "qué porcentaje de cada venta es ganancia":

```
margenObjetivo = cost_settings.default_target_margin
                 (o products.target_margin si el producto tiene override)

precioSugerido = costoTotal / (1 − margenObjetivo)
```

### 3.5 Rentabilidad real (cuando hay precio manual cargado)
Si el usuario ya fijó un precio a mano, la app muestra la ganancia real ya con indirectos incluidos:
```
gananciaUnitaria = precio − costoTotal
margenReal       = (precio − costoTotal) / precio
```
Un `margenReal` negativo indica que el producto **se vende a pérdida** una vez contados sueldos y costos fijos.

---

## 4. Ejemplo numérico end-to-end

**Parámetros del mes**
- Costos fijos: gas $30.000 + agua $10.000 + luz $40.000 + alquiler $120.000 → `F = 200.000`
- Sueldos: panadero $400.000 + ayudante $200.000 → `L = 600.000`
- Base de materiales del mes: `M = 800.000`

```
tasaIndirecta = (200.000 + 600.000) / 800.000 = 1,00  (100 %)
```
(Por cada $1 de materiales, hay $1 de costos indirectos.)

**Producto: "Bagux Tradicional"**
- Receta: 1 kg harina ($1.000) + 0,02 kg levadura ($200) + 0,02 kg sal ($40) = **$1.240** por lote
- Rendimiento del lote: 4 unidades
```
costoMateriales = 1.240 / 4                = 310
costoManoObra   = 310 × (600.000/800.000)  = 232,50
costoFijo       = 310 × (200.000/800.000)  =  77,50
costoTotal      = 310 × (1 + 1,00)         = 620
```

**Precio sugerido con margen objetivo 35 %**
```
precioSugerido = 620 / (1 − 0,35) = 620 / 0,65 ≈ 953,85 → se vende ~$954
```

**Verificación (rentabilidad real a $954)**
```
gananciaUnitaria = 954 − 620 = 334
margenReal       = 334 / 954 = 0,350 → 35 %  ✔ reconstruye el margen objetivo
```
Si el usuario vendiera a $500 (mirando solo materiales), `margenReal = (500 − 620)/500 = −24 %`: **pérdida**, invisible sin este modelo.

---

## 5. Análisis: alternativas para imputar mano de obra

El usuario pidió analizar en profundidad cómo tratar la mano de obra. Estas son las opciones evaluadas:

| Enfoque | Cómo funciona | Requiere | Veredicto |
|--------|----------------|----------|-----------|
| **Tarifa horaria × tiempo de receta** | `sueldo/hs trabajadas` → costo por hora; × horas de cada receta | Cronometrar cada receta | Descartado: el negocio no registra tiempos |
| **Prorrateo por volumen (kg/unid)** | Sueldos del mes ÷ producción del mes | Producción en una unidad común | Descartado: mezcla kg y unidades no sumables |
| **Absorción por costo directo** ✅ | Sueldos como parte de `tasaIndirecta` sobre materiales | Solo totales mensuales | **Elegido**: simple, sin tiempos, con datos reales |

**Sobre la idea del usuario ("sueldo promedio ÷ horas trabajadas"):** es un cálculo válido y útil para conocer el **costo por hora de trabajo** del negocio, pero para *imputarlo a un producto* haría falta saber cuántas horas lleva ese producto — dato que hoy no se captura. Por eso el costo por hora queda como **métrica informativa** (se puede mostrar en el dashboard: `L / Σ horas`), mientras que la **imputación** se hace por absorción. Si en el futuro se decide registrar tiempos por receta, se migra al método por hora (Sección 6) sin rehacer el resto del modelo.

---

## 6. Extensiones futuras (documentadas, no implementadas)

- **Costeo por hora de producción:** agregar `production_time` a `recipes`. Entonces `tasaFijaHora = F / hs productivas mes`, `tasaManoObraHora = L / hs trabajadas mes`, e imputar `× production_time`. Más preciso; requiere disciplina de registro.
- **Base de absorción alternativa:** usar producción esperada en kg-equivalente en lugar de costo de materiales, si el mix de productos se vuelve homogéneo.
- **Punto de equilibrio y simulación:** con `F`, `L` y márgenes, calcular cuántas unidades/mes se necesitan para cubrir costos, y simular escenarios de precio.

---

## 7. Glosario

- **Costo directo / materiales:** insumos consumidos por la receta.
- **Costo indirecto / overhead:** costos que no se asignan directamente (mano de obra + fijos).
- **Driver de asignación:** criterio para repartir indirectos (acá: costo directo de materiales).
- **Tasa de indirectos:** `(F + L) / M`.
- **Margen (de contribución):** porcentaje de ganancia **sobre el precio de venta**.
- **Rendimiento (yield):** cuántas unidades o kg produce un lote de la receta.
