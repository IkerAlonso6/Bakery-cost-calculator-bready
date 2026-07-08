package com.bakery.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO de ingrediente (uso de un insumo en una receta).
 * inputName y cost son de solo lectura: los completa el mapper en las respuestas.
 */
public record IngredientDTO(
        Integer id,
        @NotNull Integer inputId,
        String inputName,
        @NotNull @Positive BigDecimal quantity,
        BigDecimal cost
) {}
