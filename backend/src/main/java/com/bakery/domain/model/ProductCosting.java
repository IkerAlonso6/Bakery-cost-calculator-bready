package com.bakery.domain.model;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Resultado del costeo de un producto (ver COSTING_MODEL.md).
 * Todos los importes son por unidad de producto y están redondeados a 2 decimales.
 *
 * @param materialCost   costo de materiales
 * @param laborCost      mano de obra imputada por absorción
 * @param fixedCost      costos fijos imputados por absorción
 * @param totalCost      materiales + mano de obra + fijos
 * @param appliedMargin  margen usado para el precio sugerido (override del producto o global)
 * @param suggestedPrice precio sugerido = totalCost / (1 - appliedMargin)
 * @param realMargin     margen real (precio - totalCost) / precio; null si no hay precio manual
 */
public record ProductCosting(
        BigDecimal materialCost,
        BigDecimal laborCost,
        BigDecimal fixedCost,
        BigDecimal totalCost,
        BigDecimal appliedMargin,
        BigDecimal suggestedPrice,
        BigDecimal realMargin
) {

    /** Margen real; vacío si el producto aún no tiene precio manual fijado. */
    public Optional<BigDecimal> getRealMargin() {
        return Optional.ofNullable(realMargin);
    }
}
