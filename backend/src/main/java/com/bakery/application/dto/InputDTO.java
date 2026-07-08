package com.bakery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO de insumo. unitOfMeasure es el nombre del enum (KILOGRAM, GRAM...).
 */
public record InputDTO(
        Integer id,
        @NotBlank String name,
        @NotBlank String unitOfMeasure,
        @NotNull @Positive BigDecimal price
) {}
