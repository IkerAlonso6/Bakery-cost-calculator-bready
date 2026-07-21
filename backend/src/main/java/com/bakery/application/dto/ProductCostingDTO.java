package com.bakery.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de respuesta del costeo de un producto (GET /api/products/{id}/pricing).
 * Solo lectura: lo produce ProductCostingMapper a partir del dominio.
 *
 * - price y realMargin son null si el producto no tiene precio manual fijado.
 * - appliedMargin es el margen usado para el precio sugerido (override o global).
 * - requestedPeriod es el mes pedido (o el actual si no se especificó); resolvedPeriod
 *   es el mes de donde realmente vinieron los costos fijos/sueldos usados en el cálculo.
 *   Difieren solo cuando usedFallbackPeriod es true (mes pedido sin datos propios).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCostingDTO {
    private Integer productId;
    private String productName;
    private BigDecimal materialCost;
    private BigDecimal laborCost;
    private BigDecimal fixedCost;
    private BigDecimal totalCost;
    private BigDecimal appliedMargin;
    private BigDecimal suggestedPrice;
    private BigDecimal price;
    private BigDecimal realMargin;
    private String currency;
    private String requestedPeriod;
    private String resolvedPeriod;
    private boolean usedFallbackPeriod;
}
