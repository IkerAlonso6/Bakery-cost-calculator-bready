package com.bakery.web.controller;

import com.bakery.application.dto.ProductCostingDTO;
import com.bakery.application.dto.ProductDTO;
import com.bakery.application.dto.UpdateMarginRequest;
import com.bakery.application.dto.UpdatePriceRequest;
import com.bakery.application.exception.ProductNotFoundException;
import com.bakery.application.exception.RecipeNotFoundException;
import com.bakery.application.mapper.ProductMapper;
import com.bakery.application.service.CostingAppService;
import com.bakery.application.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.bakery.infrastructure.security.JwtService;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @MockBean
    private CostingAppService costingAppService;

    @MockBean
    private ProductMapper productMapper;

    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        productDTO = new ProductDTO(1, "Bagux Tradicional", 1, "Bagux Tradicional", null, null);
    }

    @Test
    @DisplayName("POST /api/products crea un producto y devuelve 201")
    void createReturns201WithCreatedProduct() throws Exception {
        when(productMapper.toDto(any())).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Bagux Tradicional"));
    }

    @Test
    @DisplayName("POST /api/products con name en blanco devuelve 400")
    void createReturns400WhenNameBlank() throws Exception {
        ProductDTO invalid = new ProductDTO(null, " ", 1, null, null, null);

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products con recipeId null devuelve 400")
    void createReturns400WhenRecipeIdNull() throws Exception {
        ProductDTO invalid = new ProductDTO(null, "Bagux", null, null, null, null);

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/products devuelve 404 si la receta no existe")
    void createReturns404WhenRecipeNotFound() throws Exception {
        ProductDTO dto = new ProductDTO(null, "Bagux", 99, null, null, null);
        when(productService.createProduct(any(), eq(99), any(), any())).thenThrow(new RecipeNotFoundException(99));

        mockMvc.perform(post("/api/products")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products devuelve 200 con la lista")
    void getAllReturns200WithList() throws Exception {
        when(productMapper.toDtoList(any())).thenReturn(List.of(productDTO));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/products/{id} devuelve 200 cuando existe")
    void getByIdReturns200WhenExists() throws Exception {
        when(productMapper.toDto(any())).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/products/{id} devuelve 404 cuando no existe")
    void getByIdReturns404WhenProductNotFound() throws Exception {
        when(productService.getProductById(99)).thenThrow(new ProductNotFoundException(99));

        mockMvc.perform(get("/api/products/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/products/{id}/price devuelve 200")
    void updatePriceReturns200WhenValid() throws Exception {
        when(productMapper.toDto(any())).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/1/price")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdatePriceRequest(new BigDecimal("954")))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/products/{id}/price con precio negativo devuelve 400")
    void updatePriceReturns400WhenPriceNegative() throws Exception {
        mockMvc.perform(put("/api/products/1/price")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdatePriceRequest(new BigDecimal("-1")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id}/price devuelve 404 si el producto no existe")
    void updatePriceReturns404WhenProductNotFound() throws Exception {
        when(productService.updateProductPrice(eq(99), any())).thenThrow(new ProductNotFoundException(99));

        mockMvc.perform(put("/api/products/99/price")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdatePriceRequest(new BigDecimal("100")))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/products/{id}/margin devuelve 200 con un valor numérico")
    void updateMarginReturns200WhenValid() throws Exception {
        when(productMapper.toDto(any())).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/1/margin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateMarginRequest(new BigDecimal("0.50")))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/products/{id}/margin con targetMargin null es válido y resetea al margen global")
    void updateMarginReturns200WhenTargetMarginNullResetsToGlobal() throws Exception {
        when(productMapper.toDto(any())).thenReturn(productDTO);

        mockMvc.perform(put("/api/products/1/margin")
                        .contentType("application/json")
                        .content("{\"targetMargin\": null}"))
                .andExpect(status().isOk());

        verify(productService).updateProductTargetMargin(1, null);
    }

    @Test
    @DisplayName("PUT /api/products/{id}/margin fuera de rango devuelve 400")
    void updateMarginReturns400WhenTargetMarginOutOfRange() throws Exception {
        mockMvc.perform(put("/api/products/1/margin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateMarginRequest(new BigDecimal("1.0")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/products/{id}/margin devuelve 404 si el producto no existe")
    void updateMarginReturns404WhenProductNotFound() throws Exception {
        when(productService.updateProductTargetMargin(eq(99), any())).thenThrow(new ProductNotFoundException(99));

        mockMvc.perform(put("/api/products/99/margin")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateMarginRequest(new BigDecimal("0.5")))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products/{id}/pricing delega en CostingAppService, no en ProductService")
    void getPricingReturns200AndDelegatesToCostingAppServiceNotProductService() throws Exception {
        ProductCostingDTO pricingDTO = new ProductCostingDTO(
                1, "Bagux Tradicional",
                new BigDecimal("310.00"), new BigDecimal("232.50"), new BigDecimal("77.50"), new BigDecimal("620.00"),
                new BigDecimal("0.35"), new BigDecimal("953.85"), null, null, "ARS");
        when(costingAppService.getProductPricing(1)).thenReturn(pricingDTO);

        mockMvc.perform(get("/api/products/1/pricing"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.suggestedPrice").value(953.85));

        verify(costingAppService).getProductPricing(1);
        verifyNoInteractions(productService);
    }

    @Test
    @DisplayName("GET /api/products/{id}/pricing devuelve 404 si el producto no existe")
    void getPricingReturns404WhenProductNotFound() throws Exception {
        when(costingAppService.getProductPricing(99)).thenThrow(new ProductNotFoundException(99));

        mockMvc.perform(get("/api/products/99/pricing"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/products/{id}/pricing devuelve 409 si la configuración de costeo no está lista")
    void getPricingReturns409WhenCostSettingsNotConfigured() throws Exception {
        when(costingAppService.getProductPricing(1))
                .thenThrow(new IllegalStateException("Cost settings are not configured yet."));

        mockMvc.perform(get("/api/products/1/pricing"))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} devuelve 204 cuando existe")
    void deleteReturns204WhenExists() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/products/{id} devuelve 404 cuando no existe")
    void deleteReturns404WhenProductNotFound() throws Exception {
        doThrow(new ProductNotFoundException(99)).when(productService).deleteProduct(99);

        mockMvc.perform(delete("/api/products/99"))
                .andExpect(status().isNotFound());
    }
}
