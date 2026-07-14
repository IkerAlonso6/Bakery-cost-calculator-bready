package com.bakery.application.service;

import com.bakery.application.dto.IngredientDTO;
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
        return createRecipe(name, yieldQuantity, yieldUnit, null);
    }

    /**
     * Crea una receta y, opcionalmente, carga sus ingredientes iniciales.
     * Cada ingrediente resuelve su Input por id (404 si no existe) y respeta
     * la regla de dominio de no repetir el mismo insumo.
     */
    public Recipe createRecipe(String name, BigDecimal yieldQuantity, UnitOfMeasurement yieldUnit,
                               List<IngredientDTO> ingredients) {
        Recipe recipe = new Recipe(name, yieldQuantity, yieldUnit);
        if (ingredients != null) {
            for (IngredientDTO ing : ingredients) {
                Input input = inputService.getInputById(ing.getInputId());
                recipe.addIngredient(new Ingredient(input, ing.getQuantity()));
            }
        }
        return recipeRepository.save(recipe);
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

    /**
     * Quita un ingrediente de la receta (por id de ingrediente).
     */
    public Recipe removeIngredientFromRecipe(Integer recipeId, Integer ingredientId) {
        Recipe recipe = getRecipeById(recipeId);
        recipe.removeIngredient(ingredientId);
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
