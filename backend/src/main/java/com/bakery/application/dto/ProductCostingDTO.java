package com.bakery.application.dto;

import java.math.BigDecimal;

/**
 * DTO de respuesta del costeo de un producto (GET /api/products/{id}/pricing).
 * Solo lectura: lo produce ProductCostingMapper a partir del dominio.
 *
 * - price y realMargin son null si el producto no tiene precio manual fijado.
 * - appliedMargin es el margen usado para el precio sugerido (override o global).
 */
public record ProductCostingDTO(
        Integer productId,
        String productName,
        BigDecimal materialCost,
        BigDecimal laborCost,
        BigDecimal fixedCost,
        BigDecimal totalCost,
        BigDecimal appliedMargin,
        BigDecimal suggestedPrice,
        BigDecimal price,
        BigDecimal realMargin,
        String currency
) {}
