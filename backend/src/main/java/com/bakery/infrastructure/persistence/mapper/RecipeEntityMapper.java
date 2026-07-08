package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.Recipe;
import com.bakery.infrastructure.persistence.entity.RecipeEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte Recipe (dominio) <-> RecipeEntity, incluyendo sus ingredientes.
 */
@Component
public class RecipeEntityMapper {

    private final IngredientEntityMapper ingredientEntityMapper;
    private final UnitOfMeasurementEntityMapper unitMapper;

    public RecipeEntityMapper(IngredientEntityMapper ingredientEntityMapper,
                              UnitOfMeasurementEntityMapper unitMapper) {
        this.ingredientEntityMapper = ingredientEntityMapper;
        this.unitMapper = unitMapper;
    }

    public RecipeEntity toEntity(Recipe recipe) {
        RecipeEntity entity = new RecipeEntity(
                recipe.getId(),
                recipe.getName(),
                recipe.getYieldQuantity(),
                unitMapper.toEntity(recipe.getYieldUnit())
        );
        recipe.getIngredients().forEach(ingredient ->
                entity.addIngredient(ingredientEntityMapper.toEntity(ingredient)));
        return entity;
    }

    public Recipe toDomain(RecipeEntity entity) {
        Recipe recipe = new Recipe(
                entity.getId(),
                entity.getName(),
                entity.getYieldQuantity(),
                unitMapper.toDomain(entity.getYieldUnit())
        );
        entity.getIngredients().forEach(ingredientEntity ->
                recipe.addIngredient(ingredientEntityMapper.toDomain(ingredientEntity)));
        return recipe;
    }
}
