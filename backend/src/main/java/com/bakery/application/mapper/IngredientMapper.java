package com.bakery.application.mapper;

import com.bakery.application.dto.IngredientDTO;
import com.bakery.domain.model.Ingredient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convierte Ingredient (dominio) -> IngredientDTO.
 * La conversión DTO -> dominio la hace RecipeService (necesita resolver
 * el Input por id contra el repositorio).
 */
@Component
public class IngredientMapper {

    public IngredientDTO toDto(Ingredient ingredient) {
        return new IngredientDTO(
                ingredient.getId(),
                ingredient.getInput().getId(),
                ingredient.getInput().getName(),
                ingredient.getQuantity(),
                ingredient.calculateCost()
        );
    }

    public List<IngredientDTO> toDtoList(List<Ingredient> ingredients) {
        return ingredients.stream().map(this::toDto).collect(Collectors.toList());
    }
}
