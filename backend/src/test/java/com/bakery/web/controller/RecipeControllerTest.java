package com.bakery.web.controller;

import com.bakery.application.dto.IngredientDTO;
import com.bakery.application.dto.RecipeDTO;
import com.bakery.application.exception.InputNotFoundException;
import com.bakery.application.exception.RecipeNotFoundException;
import com.bakery.application.mapper.RecipeMapper;
import com.bakery.application.service.RecipeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeController.class)
class RecipeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RecipeService recipeService;

    @MockBean
    private RecipeMapper recipeMapper;

    private RecipeDTO recipeDTO;

    @BeforeEach
    void setUp() {
        recipeDTO = new RecipeDTO(1, "Bagux Tradicional", new BigDecimal("4"), "UNIT", List.of());
    }

    @Test
    @DisplayName("POST /api/recipes crea una receta y devuelve 201")
    void createReturns201WithCreatedRecipe() throws Exception {
        when(recipeMapper.toDto(any())).thenReturn(recipeDTO);

        mockMvc.perform(post("/api/recipes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(recipeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bagux Tradicional"));
    }

    @Test
    @DisplayName("POST /api/recipes con name en blanco devuelve 400")
    void createReturns400WhenNameBlank() throws Exception {
        RecipeDTO invalid = new RecipeDTO(null, " ", new BigDecimal("4"), "UNIT", null);

        mockMvc.perform(post("/api/recipes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/recipes con yieldQuantity no positivo devuelve 400")
    void createReturns400WhenYieldQuantityNotPositive() throws Exception {
        RecipeDTO invalid = new RecipeDTO(null, "Bagux", BigDecimal.ZERO, "UNIT", null);

        mockMvc.perform(post("/api/recipes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/recipes con yieldUnit en blanco devuelve 400")
    void createReturns400WhenYieldUnitBlank() throws Exception {
        RecipeDTO invalid = new RecipeDTO(null, "Bagux", new BigDecimal("4"), " ", null);

        mockMvc.perform(post("/api/recipes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/recipes devuelve 200 con la lista")
    void getAllReturns200WithList() throws Exception {
        when(recipeMapper.toDtoList(any())).thenReturn(List.of(recipeDTO));

        mockMvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Bagux Tradicional"));
    }

    @Test
    @DisplayName("GET /api/recipes/{id} devuelve 200 cuando existe")
    void getByIdReturns200WhenExists() throws Exception {
        when(recipeMapper.toDto(any())).thenReturn(recipeDTO);

        mockMvc.perform(get("/api/recipes/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/recipes/{id} devuelve 404 cuando no existe")
    void getByIdReturns404WhenRecipeNotFound() throws Exception {
        when(recipeService.getRecipeById(99)).thenThrow(new RecipeNotFoundException(99));

        mockMvc.perform(get("/api/recipes/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients agrega un ingrediente y devuelve 200 (no 201)")
    void addIngredientReturns200NotCreated() throws Exception {
        IngredientDTO ingredientDTO = new IngredientDTO(null, 1, null, new BigDecimal("2"), null);
        when(recipeMapper.toDto(any())).thenReturn(recipeDTO);

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ingredientDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients con inputId null devuelve 400")
    void addIngredientReturns400WhenInputIdNull() throws Exception {
        IngredientDTO invalid = new IngredientDTO(null, null, null, new BigDecimal("2"), null);

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients con quantity no positiva devuelve 400")
    void addIngredientReturns400WhenQuantityNotPositive() throws Exception {
        IngredientDTO invalid = new IngredientDTO(null, 1, null, BigDecimal.ZERO, null);

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients devuelve 404 si la receta no existe")
    void addIngredientReturns404WhenRecipeNotFound() throws Exception {
        IngredientDTO ingredientDTO = new IngredientDTO(null, 1, null, new BigDecimal("2"), null);
        when(recipeService.addIngredientToRecipe(eq(99), any(), any())).thenThrow(new RecipeNotFoundException(99));

        mockMvc.perform(post("/api/recipes/99/ingredients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ingredientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients devuelve 404 si el insumo no existe")
    void addIngredientReturns404WhenInputNotFound() throws Exception {
        IngredientDTO ingredientDTO = new IngredientDTO(null, 99, null, new BigDecimal("2"), null);
        when(recipeService.addIngredientToRecipe(eq(1), eq(99), any())).thenThrow(new InputNotFoundException(99));

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ingredientDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/recipes/{id}/ingredients devuelve 400 si el insumo está duplicado")
    void addIngredientReturns400WhenInputDuplicated() throws Exception {
        IngredientDTO ingredientDTO = new IngredientDTO(null, 1, null, new BigDecimal("2"), null);
        when(recipeService.addIngredientToRecipe(eq(1), eq(1), any()))
                .thenThrow(new IllegalArgumentException("Input 'Harina' is already in recipe 'Bagux'"));

        mockMvc.perform(post("/api/recipes/1/ingredients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(ingredientDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/recipes/{id}/cost devuelve 200 con el número plano")
    void getCostReturns200WithPlainBigDecimalBody() throws Exception {
        when(recipeService.calculateRecipeCost(1)).thenReturn(new BigDecimal("310.0000000000"));

        mockMvc.perform(get("/api/recipes/1/cost"))
                .andExpect(status().isOk())
                .andExpect(content().string("310.0000000000"));
    }

    @Test
    @DisplayName("GET /api/recipes/{id}/cost devuelve 404 si la receta no existe")
    void getCostReturns404WhenRecipeNotFound() throws Exception {
        when(recipeService.calculateRecipeCost(99)).thenThrow(new RecipeNotFoundException(99));

        mockMvc.perform(get("/api/recipes/99/cost"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/recipes/{id} devuelve 204 cuando existe")
    void deleteReturns204WhenExists() throws Exception {
        mockMvc.perform(delete("/api/recipes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/recipes/{id} devuelve 404 cuando no existe")
    void deleteReturns404WhenRecipeNotFound() throws Exception {
        doThrow(new RecipeNotFoundException(99)).when(recipeService).deleteRecipe(99);

        mockMvc.perform(delete("/api/recipes/99"))
                .andExpect(status().isNotFound());
    }
}
