package com.bakery.application.service;

import com.bakery.application.exception.InputNotFoundException;
import com.bakery.application.exception.RecipeNotFoundException;
import com.bakery.application.port.IRecipeRepository;
import com.bakery.domain.model.Ingredient;
import com.bakery.domain.model.Input;
import com.bakery.domain.model.Recipe;
import com.bakery.domain.model.UnitOfMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private IRecipeRepository recipeRepository;

    @Mock
    private InputService inputService;

    @InjectMocks
    private RecipeService recipeService;

    private Recipe recipe;
    private Input harina;

    @BeforeEach
    void setUp() {
        recipe = new Recipe(1, "Bagux Tradicional", new BigDecimal("4"), UnitOfMeasurement.UNIT);
        harina = new Input(1, "Harina 000", UnitOfMeasurement.KILOGRAM, new BigDecimal("1000"));
    }

    @Test
    @DisplayName("Crea una receta y la guarda")
    void createRecipeSavesAndReturnsRecipe() {
        when(recipeRepository.save(any(Recipe.class))).thenReturn(recipe);

        Recipe created = recipeService.createRecipe("Bagux Tradicional", new BigDecimal("4"), UnitOfMeasurement.UNIT);

        assertEquals(recipe, created);
    }

    @Test
    @DisplayName("Obtiene una receta existente por id")
    void getRecipeByIdReturnsRecipeWhenExists() {
        when(recipeRepository.findById(1)).thenReturn(Optional.of(recipe));

        assertEquals(recipe, recipeService.getRecipeById(1));
    }

    @Test
    @DisplayName("Lanza RecipeNotFoundException si la receta no existe")
    void getRecipeByIdThrowsRecipeNotFoundExceptionWhenMissing() {
        when(recipeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.getRecipeById(99));
    }

    @Test
    @DisplayName("Devuelve todas las recetas del repositorio")
    void getAllRecipesReturnsRepositoryList() {
        when(recipeRepository.findAll()).thenReturn(List.of(recipe));

        assertEquals(1, recipeService.getAllRecipes().size());
    }

    @Test
    @DisplayName("Agrega un ingrediente a la receta y la guarda")
    void addIngredientToRecipeAddsAndSavesWhenValid() {
        when(recipeRepository.findById(1)).thenReturn(Optional.of(recipe));
        when(inputService.getInputById(1)).thenReturn(harina);
        when(recipeRepository.save(recipe)).thenReturn(recipe);

        Recipe updated = recipeService.addIngredientToRecipe(1, 1, new BigDecimal("2"));

        assertEquals(1, updated.getIngredients().size());
        assertEquals(harina, updated.getIngredients().get(0).getInput());
    }

    @Test
    @DisplayName("addIngredientToRecipe lanza RecipeNotFoundException y nunca resuelve el insumo")
    void addIngredientToRecipeThrowsRecipeNotFoundExceptionWhenRecipeMissing() {
        when(recipeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class,
                () -> recipeService.addIngredientToRecipe(99, 1, new BigDecimal("2")));
        verifyNoInteractions(inputService);
    }

    @Test
    @DisplayName("addIngredientToRecipe lanza InputNotFoundException si el insumo no existe")
    void addIngredientToRecipeThrowsInputNotFoundExceptionWhenInputMissing() {
        when(recipeRepository.findById(1)).thenReturn(Optional.of(recipe));
        when(inputService.getInputById(99)).thenThrow(new InputNotFoundException(99));

        assertThrows(InputNotFoundException.class,
                () -> recipeService.addIngredientToRecipe(1, 99, new BigDecimal("2")));
        verify(recipeRepository, never()).save(any());
    }

    @Test
    @DisplayName("addIngredientToRecipe rechaza insumos duplicados y no guarda")
    void addIngredientToRecipeThrowsIllegalArgumentExceptionWhenInputDuplicated() {
        recipe.addIngredient(new Ingredient(harina, new BigDecimal("1")));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(recipe));
        when(inputService.getInputById(1)).thenReturn(harina);

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.addIngredientToRecipe(1, 1, new BigDecimal("2")));
        verify(recipeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Calcula el costo por unidad de la receta")
    void calculateRecipeCostDelegatesToRecipeDomain() {
        recipe.addIngredient(new Ingredient(harina, new BigDecimal("1")));
        when(recipeRepository.findById(1)).thenReturn(Optional.of(recipe));

        BigDecimal cost = recipeService.calculateRecipeCost(1);

        assertEquals(0, new BigDecimal("250").compareTo(cost)); // 1000 / 4
    }

    @Test
    @DisplayName("calculateRecipeCost lanza RecipeNotFoundException si no existe")
    void calculateRecipeCostThrowsWhenRecipeNotFound() {
        when(recipeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.calculateRecipeCost(99));
    }

    @Test
    @DisplayName("Elimina una receta existente")
    void deleteRecipeDeletesWhenExists() {
        when(recipeRepository.findById(1)).thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(1);

        verify(recipeRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteRecipe lanza RecipeNotFoundException si no existe")
    void deleteRecipeThrowsWhenNotFound() {
        when(recipeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RecipeNotFoundException.class, () -> recipeService.deleteRecipe(99));
        verify(recipeRepository, never()).deleteById(any());
    }
}
