package com.bakery.application.mapper;

import com.bakery.application.dto.RecipeDTO;
import com.bakery.domain.model.Recipe;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte Recipe (dominio) <-> RecipeDTO (incluye ingredientes).
 */
@Component
public class RecipeMapper {

    private final IngredientMapper ingredientMapper;

    public RecipeMapper(IngredientMapper ingredientMapper) {
        this.ingredientMapper = ingredientMapper;
    }

    public RecipeDTO toDto(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getName(),
                recipe.getYieldQuantity(),
                recipe.getYieldUnit().name(),
                ingredientMapper.toDtoList(recipe.getIngredients())
        );
    }

    public List<RecipeDTO> toDtoList(List<Recipe> recipes) {
        return recipes.stream().map(this::toDto).toList();
    }

    /** Crea la receta de dominio (sin ingredientes; se agregan por endpoint propio). */
    public Recipe toDomain(RecipeDTO dto) {
        return new Recipe(
                dto.name(),
                dto.yieldQuantity(),
                InputMapper.parseUnit(dto.yieldUnit())
        );
    }
}
