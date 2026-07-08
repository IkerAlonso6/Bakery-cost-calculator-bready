package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.Ingredient;
import com.bakery.infrastructure.persistence.entity.IngredientEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte Ingredient (dominio) <-> IngredientEntity.
 * La referencia a la receta la fija RecipeEntity.addIngredient(...)
 * al armar el agregado.
 */
@Component
public class IngredientEntityMapper {

    private final InputEntityMapper inputEntityMapper;

    public IngredientEntityMapper(InputEntityMapper inputEntityMapper) {
        this.inputEntityMapper = inputEntityMapper;
    }

    public IngredientEntity toEntity(Ingredient ingredient) {
        return new IngredientEntity(
                ingredient.getId(),
                inputEntityMapper.toEntity(ingredient.getInput()),
                ingredient.getQuantity()
        );
    }

    public Ingredient toDomain(IngredientEntity entity) {
        return new Ingredient(
                entity.getId(),
                inputEntityMapper.toDomain(entity.getInput()),
                entity.getQuantity()
        );
    }
}
