package com.bakery.web.controller;

import com.bakery.application.dto.InputDTO;
import com.bakery.application.dto.UpdatePriceRequest;
import com.bakery.application.exception.InputNotFoundException;
import com.bakery.application.mapper.InputMapper;
import com.bakery.application.service.InputService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InputController.class)
class InputControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InputService inputService;

    @MockBean
    private InputMapper inputMapper;

    private InputDTO inputDTO;

    @BeforeEach
    void setUp() {
        inputDTO = new InputDTO(1, "Harina 000", "KILOGRAM", new BigDecimal("1000"));
    }

    @Test
    @DisplayName("POST /api/inputs crea un insumo y devuelve 201")
    void createReturns201WithCreatedInput() throws Exception {
        when(inputMapper.toDto(any())).thenReturn(inputDTO);

        mockMvc.perform(post("/api/inputs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Harina 000"));
    }

    @Test
    @DisplayName("POST /api/inputs con name en blanco devuelve 400")
    void createReturns400WhenNameBlank() throws Exception {
        InputDTO invalid = new InputDTO(null, " ", "KILOGRAM", new BigDecimal("1000"));

        mockMvc.perform(post("/api/inputs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    @DisplayName("POST /api/inputs con precio negativo devuelve 400")
    void createReturns400WhenPriceNegativeOrZero() throws Exception {
        InputDTO invalid = new InputDTO(null, "Harina", "KILOGRAM", new BigDecimal("-1"));

        mockMvc.perform(post("/api/inputs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/inputs con unitOfMeasure en blanco devuelve 400")
    void createReturns400WhenUnitOfMeasureBlank() throws Exception {
        InputDTO invalid = new InputDTO(null, "Harina", " ", new BigDecimal("1000"));

        mockMvc.perform(post("/api/inputs")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/inputs devuelve 200 con la lista")
    void getAllReturns200WithList() throws Exception {
        when(inputMapper.toDtoList(any())).thenReturn(List.of(inputDTO));

        mockMvc.perform(get("/api/inputs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Harina 000"));
    }

    @Test
    @DisplayName("GET /api/inputs/{id} devuelve 200 cuando existe")
    void getByIdReturns200WhenExists() throws Exception {
        when(inputMapper.toDto(any())).thenReturn(inputDTO);

        mockMvc.perform(get("/api/inputs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /api/inputs/{id} devuelve 404 cuando no existe")
    void getByIdReturns404WhenInputNotFound() throws Exception {
        when(inputService.getInputById(99)).thenThrow(new InputNotFoundException(99));

        mockMvc.perform(get("/api/inputs/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Input not found with id: 99"));
    }

    @Test
    @DisplayName("PUT /api/inputs/{id}/price devuelve 200 con precio actualizado")
    void updatePriceReturns200WhenValid() throws Exception {
        when(inputMapper.toDto(any())).thenReturn(inputDTO);

        mockMvc.perform(put("/api/inputs/1/price")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdatePriceRequest(new BigDecimal("1200")))))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/inputs/{id}/price con precio null devuelve 400")
    void updatePriceReturns400WhenPriceNull() throws Exception {
        mockMvc.perform(put("/api/inputs/1/price")
                        .contentType("application/json")
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/inputs/{id}/price con precio negativo devuelve 400")
    void updatePriceReturns400WhenPriceNegative() throws Exception {
        mockMvc.perform(put("/api/inputs/1/price")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdatePriceRequest(new BigDecimal("-1")))))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/inputs/{id}/price devuelve 404 cuando el insumo no existe")
    void updatePriceReturns404WhenInputNotFound() throws Exception {
        when(inputService.updateInputPrice(eq(99), any())).thenThrow(new InputNotFoundException(99));

        mockMvc.perform(put("/api/inputs/99/price")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdatePriceRequest(new BigDecimal("100")))))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/inputs/{id} devuelve 204 cuando existe")
    void deleteReturns204WhenExists() throws Exception {
        mockMvc.perform(delete("/api/inputs/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/inputs/{id} devuelve 404 cuando no existe")
    void deleteReturns404WhenInputNotFound() throws Exception {
        doThrow(new InputNotFoundException(99)).when(inputService).deleteInput(99);

        mockMvc.perform(delete("/api/inputs/99"))
                .andExpect(status().isNotFound());
    }
}
