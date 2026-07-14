package com.bakery.application.mapper;

import com.bakery.application.dto.RecipeDTO;
import com.bakery.domain.model.Recipe;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

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
        RecipeDTO dto = new RecipeDTO();
        dto.setId(recipe.getId());
        dto.setName(recipe.getName());
        dto.setYieldQuantity(recipe.getYieldQuantity());
        dto.setYieldUnit(recipe.getYieldUnit().name());
        dto.setIngredients(ingredientMapper.toDtoList(recipe.getIngredients()));
        return dto;
    }

    public List<RecipeDTO> toDtoList(List<Recipe> recipes) {
        return recipes.stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Crea la receta de dominio (sin ingredientes; se agregan por endpoint propio). */
    public Recipe toDomain(RecipeDTO dto) {
        return new Recipe(
                dto.getName(),
                dto.getYieldQuantity(),
                InputMapper.parseUnit(dto.getYieldUnit())
        );
    }
}
