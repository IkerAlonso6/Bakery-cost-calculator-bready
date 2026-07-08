package com.bakery.application.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO de la configuración global de costeo (fila única).
 */
public record CostSettingsDTO(
        @NotNull @DecimalMin("0.0") @DecimalMax(value = "1.0", inclusive = false) BigDecimal defaultTargetMargin,
        @NotNull @Positive BigDecimal monthlyMaterialBase,
        @NotBlank String currency
) {}
