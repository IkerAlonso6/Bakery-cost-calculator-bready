package com.bakery.web.controller;

import com.bakery.application.dto.IngredientDTO;
import com.bakery.application.dto.RecipeDTO;
import com.bakery.application.mapper.InputMapper;
import com.bakery.application.mapper.RecipeMapper;
import com.bakery.application.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeMapper recipeMapper;

    public RecipeController(RecipeService recipeService, RecipeMapper recipeMapper) {
        this.recipeService = recipeService;
        this.recipeMapper = recipeMapper;
    }

    @PostMapping
    public ResponseEntity<RecipeDTO> create(@Valid @RequestBody RecipeDTO dto) {
        var created = recipeService.createRecipe(
                dto.name(),
                dto.yieldQuantity(),
                InputMapper.parseUnit(dto.yieldUnit()));
        return ResponseEntity.status(HttpStatus.CREATED).body(recipeMapper.toDto(created));
    }

    @GetMapping
    public List<RecipeDTO> getAll() {
        return recipeMapper.toDtoList(recipeService.getAllRecipes());
    }

    @GetMapping("/{id}")
    public RecipeDTO getById(@PathVariable Integer id) {
        return recipeMapper.toDto(recipeService.getRecipeById(id));
    }

    @PostMapping("/{id}/ingredients")
    public RecipeDTO addIngredient(@PathVariable Integer id,
                                   @Valid @RequestBody IngredientDTO ingredient) {
        var updated = recipeService.addIngredientToRecipe(id, ingredient.inputId(), ingredient.quantity());
        return recipeMapper.toDto(updated);
    }

    /** Costo de materiales por unidad de rendimiento (kg o unidad producida). */
    @GetMapping("/{id}/cost")
    public BigDecimal getCost(@PathVariable Integer id) {
        return recipeService.calculateRecipeCost(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }
}
