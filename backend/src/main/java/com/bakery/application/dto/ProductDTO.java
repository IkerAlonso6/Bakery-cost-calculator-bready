package com.bakery.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO de producto. price y targetMargin son opcionales:
 * - price null = todavía sin precio manual fijado.
 * - targetMargin null = usa el margen global de cost_settings.
 * recipeName es de solo lectura (lo completa el mapper).
 */
public record ProductDTO(
        Integer id,
        @NotBlank String name,
        @NotNull Integer recipeId,
        String recipeName,
        @PositiveOrZero BigDecimal price,
        @DecimalMin("0.0") @DecimalMax(value = "1.0", inclusive = false) BigDecimal targetMargin
) {}
