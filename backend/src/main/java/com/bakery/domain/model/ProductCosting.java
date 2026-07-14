package com.bakery.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Resultado del costeo de un producto (ver COSTING_MODEL.md).
 * Todos los importes son por unidad de producto y están redondeados a 2 decimales.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCosting {
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal fixedCost;
    private BigDecimal totalCost;
    private BigDecimal appliedMargin;
    private BigDecimal suggestedPrice;
    private BigDecimal realMargin;

    public Optional<BigDecimal> getRealMargin() {
        return Optional.ofNullable(realMargin);
    }
}
