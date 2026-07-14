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
}
