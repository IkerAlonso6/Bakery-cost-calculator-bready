package com.bakery.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

/**
 * Body de PUT /api/products/{id}/margin.
 * targetMargin null = volver al margen global del negocio.
 */
public record UpdateMarginRequest(
        @DecimalMin("0.0") @DecimalMax(value = "1.0", inclusive = false) BigDecimal targetMargin
) {}
