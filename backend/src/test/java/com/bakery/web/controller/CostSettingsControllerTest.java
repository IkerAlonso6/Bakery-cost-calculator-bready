package com.bakery.web.controller;

import com.bakery.application.dto.CostSettingsDTO;
import com.bakery.application.mapper.CostSettingsMapper;
import com.bakery.application.service.CostSettingsService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CostSettingsController.class)
@AutoConfigureMockMvc(addFilters = false)
class CostSettingsControllerTest {

    @MockBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CostSettingsService costSettingsService;

    @MockBean
    private CostSettingsMapper costSettingsMapper;

    private CostSettingsDTO costSettingsDTO;

    @BeforeEach
    void setUp() {
        costSettingsDTO = new CostSettingsDTO(new BigDecimal("0.35"), new BigDecimal("800000"), "ARS");
    }

    @Test
    @DisplayName("GET /api/cost-settings devuelve 200 cuando está configurado")
    void getReturns200WithSettingsWhenConfigured() throws Exception {
        when(costSettingsMapper.toDto(any())).thenReturn(costSettingsDTO);

        mockMvc.perform(get("/api/cost-settings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("ARS"));
    }

    @Test
    @DisplayName("GET /api/cost-settings devuelve 409 cuando no está configurado")
    void getReturns409WhenNotConfigured() throws Exception {
        when(costSettingsService.getSettings())
                .thenThrow(new IllegalStateException("Cost settings are not configured yet."));

        mockMvc.perform(get("/api/cost-settings"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    @DisplayName("PUT /api/cost-settings devuelve 200 con la configuración actualizada")
    void updateReturns200WithUpdatedSettings() throws Exception {
        when(costSettingsMapper.toDto(any())).thenReturn(costSettingsDTO);

        mockMvc.perform(put("/api/cost-settings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(costSettingsDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/cost-settings con defaultTargetMargin fuera de rango devuelve 400")
    void updateReturns400WhenDefaultTargetMarginOutOfRange() throws Exception {
        CostSettingsDTO invalid = new CostSettingsDTO(new BigDecimal("1.0"), new BigDecimal("800000"), "ARS");

        mockMvc.perform(put("/api/cost-settings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/cost-settings con monthlyMaterialBase no positivo devuelve 400")
    void updateReturns400WhenMonthlyMaterialBaseNotPositive() throws Exception {
        CostSettingsDTO invalid = new CostSettingsDTO(new BigDecimal("0.35"), BigDecimal.ZERO, "ARS");

        mockMvc.perform(put("/api/cost-settings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/cost-settings con currency en blanco devuelve 400")
    void updateReturns400WhenCurrencyBlank() throws Exception {
        CostSettingsDTO invalid = new CostSettingsDTO(new BigDecimal("0.35"), new BigDecimal("800000"), " ");

        mockMvc.perform(put("/api/cost-settings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }
}
