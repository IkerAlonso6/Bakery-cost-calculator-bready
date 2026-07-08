package com.bakery.application.service;

import com.bakery.application.exception.RecipeNotFoundException;
import com.bakery.application.port.IRecipeRepository;
import com.bakery.domain.model.Ingredient;
import com.bakery.domain.model.Input;
import com.bakery.domain.model.Recipe;
import com.bakery.domain.model.UnitOfMeasurement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso de recetas y sus ingredientes.
 */
@Service
public class RecipeService {

    private final IRecipeRepository recipeRepository;
    private final InputService inputService;

    public RecipeService(IRecipeRepository recipeRepository, InputService inputService) {
        this.recipeRepository = recipeRepository;
        this.inputService = inputService;
    }

    public Recipe createRecipe(String name, BigDecimal yieldQuantity, UnitOfMeasurement yieldUnit) {
        return recipeRepository.save(new Recipe(name, yieldQuantity, yieldUnit));
    }

    public Recipe getRecipeById(Integer id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RecipeNotFoundException(id));
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    /**
     * Agrega un insumo a la receta. Resuelve el Input por id (404 si no existe)
     * y delega en el dominio la regla de no repetir insumos.
     */
    public Recipe addIngredientToRecipe(Integer recipeId, Integer inputId, BigDecimal quantity) {
        Recipe recipe = getRecipeById(recipeId);
        Input input = inputService.getInputById(inputId);
        recipe.addIngredient(new Ingredient(input, quantity));
        return recipeRepository.save(recipe);
    }

    /** Costo de materiales por unidad de rendimiento (kg o unidad). */
    public BigDecimal calculateRecipeCost(Integer recipeId) {
        return getRecipeById(recipeId).calculateCostPerUnit();
    }

    public void deleteRecipe(Integer id) {
        getRecipeById(id); // 404 si no existe
        recipeRepository.deleteById(id);
    }
}
