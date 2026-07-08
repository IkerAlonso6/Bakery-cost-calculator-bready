package com.bakery.web.controller;

import com.bakery.application.dto.FixedCostDTO;
import com.bakery.application.exception.FixedCostNotFoundException;
import com.bakery.application.mapper.FixedCostMapper;
import com.bakery.application.service.FixedCostService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FixedCostController.class)
class FixedCostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FixedCostService fixedCostService;

    @MockBean
    private FixedCostMapper fixedCostMapper;

    private FixedCostDTO fixedCostDTO;

    @BeforeEach
    void setUp() {
        fixedCostDTO = new FixedCostDTO(1, "Gas", new BigDecimal("30000"));
    }

    @Test
    @DisplayName("POST /api/fixed-costs crea un costo fijo y devuelve 201")
    void createReturns201WithCreatedFixedCost() throws Exception {
        when(fixedCostMapper.toDto(any())).thenReturn(fixedCostDTO);

        mockMvc.perform(post("/api/fixed-costs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(fixedCostDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/fixed-costs con name en blanco devuelve 400")
    void createReturns400WhenNameBlank() throws Exception {
        FixedCostDTO invalid = new FixedCostDTO(null, " ", new BigDecimal("100"));

        mockMvc.perform(post("/api/fixed-costs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/fixed-costs con monthlyAmount negativo devuelve 400")
    void createReturns400WhenMonthlyAmountNegative() throws Exception {
        FixedCostDTO invalid = new FixedCostDTO(null, "Gas", new BigDecimal("-1"));

        mockMvc.perform(post("/api/fixed-costs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/fixed-costs devuelve 200 con la lista")
    void getAllReturns200WithList() throws Exception {
        when(fixedCostMapper.toDtoList(any())).thenReturn(List.of(fixedCostDTO));

        mockMvc.perform(get("/api/fixed-costs"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/fixed-costs/total devuelve 200 con el número plano (no colisiona con /{id})")
    void getMonthlyTotalReturns200WithPlainNumber() throws Exception {
        when(fixedCostService.getMonthlyTotal()).thenReturn(new BigDecimal("200000"));

        mockMvc.perform(get("/api/fixed-costs/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("200000"));
    }

    @Test
    @DisplayName("GET /api/fixed-costs/{id} devuelve 200 cuando existe")
    void getByIdReturns200WhenExists() throws Exception {
        when(fixedCostMapper.toDto(any())).thenReturn(fixedCostDTO);

        mockMvc.perform(get("/api/fixed-costs/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/fixed-costs/{id} devuelve 404 cuando no existe")
    void getByIdReturns404WhenFixedCostNotFound() throws Exception {
        when(fixedCostService.getFixedCostById(99)).thenThrow(new FixedCostNotFoundException(99));

        mockMvc.perform(get("/api/fixed-costs/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/fixed-costs/{id} devuelve 200")
    void updateReturns200WhenValid() throws Exception {
        when(fixedCostMapper.toDto(any())).thenReturn(fixedCostDTO);

        mockMvc.perform(put("/api/fixed-costs/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(fixedCostDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/fixed-costs/{id} con monthlyAmount negativo devuelve 400")
    void updateReturns400WhenMonthlyAmountNegative() throws Exception {
        FixedCostDTO invalid = new FixedCostDTO(1, "Gas", new BigDecimal("-1"));

        mockMvc.perform(put("/api/fixed-costs/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/fixed-costs/{id} devuelve 404 si no existe")
    void updateReturns404WhenFixedCostNotFound() throws Exception {
        when(fixedCostService.updateFixedCostAmount(eq(99), any())).thenThrow(new FixedCostNotFoundException(99));

        mockMvc.perform(put("/api/fixed-costs/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(fixedCostDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/fixed-costs/{id} devuelve 204 cuando existe")
    void deleteReturns204WhenExists() throws Exception {
        mockMvc.perform(delete("/api/fixed-costs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/fixed-costs/{id} devuelve 404 cuando no existe")
    void deleteReturns404WhenFixedCostNotFound() throws Exception {
        doThrow(new FixedCostNotFoundException(99)).when(fixedCostService).deleteFixedCost(99);

        mockMvc.perform(delete("/api/fixed-costs/99"))
                .andExpect(status().isNotFound());
    }
}
