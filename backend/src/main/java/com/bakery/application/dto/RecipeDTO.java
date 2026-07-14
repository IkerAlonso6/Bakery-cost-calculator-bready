package com.bakery.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO de receta. yieldUnit es el nombre del enum (KILOGRAM o UNIT).
 * ingredients es de solo lectura en las respuestas; los ingredientes
 * se agregan por el endpoint POST /api/recipes/{id}/ingredients.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDTO {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull @Positive
    private BigDecimal yieldQuantity;
    @NotBlank
    private String yieldUnit;
    private List<IngredientDTO> ingredients;
}
