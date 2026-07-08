package com.bakery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO de costo fijo mensual (gas, agua, luz, alquiler...).
 */
public record FixedCostDTO(
        Integer id,
        @NotBlank String name,
        @NotNull @PositiveOrZero BigDecimal monthlyAmount
) {}
