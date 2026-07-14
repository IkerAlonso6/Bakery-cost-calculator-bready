package com.bakery.application.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Body de PUT /api/products/{id}/margin.
 * targetMargin null = volver al margen global del negocio.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMarginRequest {
    @DecimalMin("0.0") @DecimalMax(value = "1.0", inclusive = false)
    private BigDecimal targetMargin;
}
