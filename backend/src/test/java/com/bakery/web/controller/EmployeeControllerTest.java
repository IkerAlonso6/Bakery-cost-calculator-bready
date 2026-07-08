package com.bakery.web.controller;

import com.bakery.application.dto.EmployeeDTO;
import com.bakery.application.exception.EmployeeNotFoundException;
import com.bakery.application.mapper.EmployeeMapper;
import com.bakery.application.service.EmployeeService;
import com.bakery.domain.model.Employee;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private EmployeeMapper employeeMapper;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = new EmployeeDTO(1, "Panadero", new BigDecimal("400000"), new BigDecimal("160"), new BigDecimal("2500.00"));
    }

    @Test
    @DisplayName("POST /api/employees crea un empleado y devuelve 201")
    void createReturns201WithCreatedEmployee() throws Exception {
        when(employeeMapper.toDto(any())).thenReturn(employeeDTO);

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/employees con name en blanco devuelve 400")
    void createReturns400WhenNameBlank() throws Exception {
        EmployeeDTO invalid = new EmployeeDTO(null, " ", new BigDecimal("400000"), new BigDecimal("160"), null);

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/employees con monthlySalary negativo devuelve 400")
    void createReturns400WhenMonthlySalaryNegative() throws Exception {
        EmployeeDTO invalid = new EmployeeDTO(null, "Panadero", new BigDecimal("-1"), new BigDecimal("160"), null);

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/employees con monthlyHours no positivo devuelve 400")
    void createReturns400WhenMonthlyHoursNotPositive() throws Exception {
        EmployeeDTO invalid = new EmployeeDTO(null, "Panadero", new BigDecimal("400000"), BigDecimal.ZERO, null);

        mockMvc.perform(post("/api/employees")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/employees devuelve 200 con la lista")
    void getAllReturns200WithList() throws Exception {
        when(employeeMapper.toDtoList(any())).thenReturn(List.of(employeeDTO));

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/employees/total devuelve 200 con el número plano")
    void getMonthlyTotalReturns200WithPlainNumber() throws Exception {
        when(employeeService.getMonthlyTotal()).thenReturn(new BigDecimal("600000"));

        mockMvc.perform(get("/api/employees/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("600000"));
    }

    @Test
    @DisplayName("GET /api/employees/{id} devuelve 200 cuando existe")
    void getByIdReturns200WhenExists() throws Exception {
        when(employeeMapper.toDto(any())).thenReturn(employeeDTO);

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/employees/{id} devuelve 404 cuando no existe")
    void getByIdReturns404WhenEmployeeNotFound() throws Exception {
        when(employeeService.getEmployeeById(99)).thenThrow(new EmployeeNotFoundException(99));

        mockMvc.perform(get("/api/employees/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/employees/{id} llama primero a updateEmployeeSalary y luego a updateEmployeeHours")
    void updateReturns200AndCallsSalaryThenHoursInOrder() throws Exception {
        Employee afterSalary = new Employee(1, "Panadero", new BigDecimal("450000"), new BigDecimal("160"));
        Employee afterHours = new Employee(1, "Panadero", new BigDecimal("450000"), new BigDecimal("180"));
        when(employeeService.updateEmployeeSalary(eq(1), any())).thenReturn(afterSalary);
        when(employeeService.updateEmployeeHours(eq(1), any())).thenReturn(afterHours);
        when(employeeMapper.toDto(afterHours)).thenReturn(employeeDTO);

        EmployeeDTO requestDto = new EmployeeDTO(null, "Panadero", new BigDecimal("450000"), new BigDecimal("180"), null);

        mockMvc.perform(put("/api/employees/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk());

        InOrder order = inOrder(employeeService);
        order.verify(employeeService).updateEmployeeSalary(1, new BigDecimal("450000"));
        order.verify(employeeService).updateEmployeeHours(1, new BigDecimal("180"));
        verify(employeeMapper).toDto(afterHours);
    }

    @Test
    @DisplayName("PUT /api/employees/{id} con monthlySalary negativo devuelve 400")
    void updateReturns400WhenMonthlySalaryNegative() throws Exception {
        EmployeeDTO invalid = new EmployeeDTO(1, "Panadero", new BigDecimal("-1"), new BigDecimal("160"), null);

        mockMvc.perform(put("/api/employees/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalid)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/employees/{id} devuelve 404 si falla la actualización del sueldo y nunca actualiza horas")
    void updateReturns404WhenEmployeeNotFoundOnSalaryUpdate() throws Exception {
        when(employeeService.updateEmployeeSalary(eq(99), any())).thenThrow(new EmployeeNotFoundException(99));

        mockMvc.perform(put("/api/employees/99")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isNotFound());

        verify(employeeService, never()).updateEmployeeHours(any(), any());
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} devuelve 204 cuando existe")
    void deleteReturns204WhenExists() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/employees/{id} devuelve 404 cuando no existe")
    void deleteReturns404WhenEmployeeNotFound() throws Exception {
        doThrow(new EmployeeNotFoundException(99)).when(employeeService).deleteEmployee(99);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}
