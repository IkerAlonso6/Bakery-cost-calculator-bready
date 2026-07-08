package com.bakery.application.service;

import com.bakery.application.exception.EmployeeNotFoundException;
import com.bakery.application.port.IEmployeeRepository;
import com.bakery.domain.model.Employee;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private IEmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee panadero;

    @BeforeEach
    void setUp() {
        panadero = new Employee(1, "Panadero", new BigDecimal("400000"), new BigDecimal("160"));
    }

    @Test
    @DisplayName("Crea un empleado y lo guarda")
    void createEmployeeSavesAndReturns() {
        when(employeeRepository.save(any(Employee.class))).thenReturn(panadero);

        Employee created = employeeService.createEmployee("Panadero", new BigDecimal("400000"), new BigDecimal("160"));

        assertEquals(panadero, created);
    }

    @Test
    @DisplayName("Obtiene un empleado existente por id")
    void getEmployeeByIdReturnsWhenExists() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));

        assertEquals(panadero, employeeService.getEmployeeById(1));
    }

    @Test
    @DisplayName("Lanza EmployeeNotFoundException si no existe")
    void getEmployeeByIdThrowsEmployeeNotFoundExceptionWhenMissing() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(99));
    }

    @Test
    @DisplayName("Devuelve todos los empleados del repositorio")
    void getAllEmployeesReturnsRepositoryList() {
        when(employeeRepository.findAll()).thenReturn(List.of(panadero));

        assertEquals(1, employeeService.getAllEmployees().size());
    }

    @Test
    @DisplayName("Actualiza el sueldo de un empleado y lo guarda")
    void updateEmployeeSalaryUpdatesAndSaves() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));
        when(employeeRepository.save(panadero)).thenReturn(panadero);

        Employee updated = employeeService.updateEmployeeSalary(1, new BigDecimal("450000"));

        assertEquals(new BigDecimal("450000"), updated.getMonthlySalary());
    }

    @Test
    @DisplayName("updateEmployeeSalary rechaza sueldo negativo")
    void updateEmployeeSalaryThrowsIllegalArgumentExceptionWhenNegative() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.updateEmployeeSalary(1, new BigDecimal("-1")));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualiza las horas mensuales de un empleado y lo guarda")
    void updateEmployeeHoursUpdatesAndSaves() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));
        when(employeeRepository.save(panadero)).thenReturn(panadero);

        Employee updated = employeeService.updateEmployeeHours(1, new BigDecimal("180"));

        assertEquals(new BigDecimal("180"), updated.getMonthlyHours().orElseThrow());
    }

    @Test
    @DisplayName("updateEmployeeHours permite null para limpiar las horas")
    void updateEmployeeHoursAllowsNullToClearHours() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));
        when(employeeRepository.save(panadero)).thenReturn(panadero);

        Employee updated = employeeService.updateEmployeeHours(1, null);

        assertTrue(updated.getMonthlyHours().isEmpty());
    }

    @Test
    @DisplayName("updateEmployeeHours rechaza valores <= 0")
    void updateEmployeeHoursThrowsIllegalArgumentExceptionWhenLessOrEqualZero() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));

        assertThrows(IllegalArgumentException.class,
                () -> employeeService.updateEmployeeHours(1, BigDecimal.ZERO));
        verify(employeeRepository, never()).save(any());
    }

    @Test
    @DisplayName("Elimina un empleado existente")
    void deleteEmployeeDeletesWhenExists() {
        when(employeeRepository.findById(1)).thenReturn(Optional.of(panadero));

        employeeService.deleteEmployee(1);

        verify(employeeRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteEmployee lanza EmployeeNotFoundException si no existe")
    void deleteEmployeeThrowsWhenNotFound() {
        when(employeeRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(99));
        verify(employeeRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getMonthlyTotal suma todos los sueldos (L = 600.000)")
    void getMonthlyTotalSumsAllSalaries() {
        List<Employee> employees = List.of(
                new Employee(1, "Panadero", new BigDecimal("400000"), new BigDecimal("160")),
                new Employee(2, "Ayudante", new BigDecimal("200000"), new BigDecimal("160"))
        );
        when(employeeRepository.findAll()).thenReturn(employees);

        BigDecimal total = employeeService.getMonthlyTotal();

        assertEquals(0, new BigDecimal("600000").compareTo(total));
    }

    @Test
    @DisplayName("getMonthlyTotal devuelve cero si no hay empleados")
    void getMonthlyTotalReturnsZeroWhenListIsEmpty() {
        when(employeeRepository.findAll()).thenReturn(List.of());

        BigDecimal total = employeeService.getMonthlyTotal();

        assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }
}
