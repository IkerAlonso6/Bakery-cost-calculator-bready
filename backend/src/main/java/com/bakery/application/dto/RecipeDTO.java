package com.bakery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de receta. yieldUnit es el nombre del enum (KILOGRAM o UNIT).
 * ingredients es de solo lectura en las respuestas; los ingredientes
 * se agregan por el endpoint POST /api/recipes/{id}/ingredients.
 */
public record RecipeDTO(
        Integer id,
        @NotBlank String name,
        @NotNull @Positive BigDecimal yieldQuantity,
        @NotBlank String yieldUnit,
        List<IngredientDTO> ingredients
) {}
