package com.bakery.web.controller;

import com.bakery.application.dto.EmployeeDTO;
import com.bakery.application.mapper.EmployeeMapper;
import com.bakery.application.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;
    private final EmployeeMapper employeeMapper;

    public EmployeeController(EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    @PostMapping
    public ResponseEntity<EmployeeDTO> create(@Valid @RequestBody EmployeeDTO dto) {
        var created = employeeService.createEmployee(dto.name(), dto.monthlySalary(), dto.monthlyHours());
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeMapper.toDto(created));
    }

    @GetMapping
    public List<EmployeeDTO> getAll() {
        return employeeMapper.toDtoList(employeeService.getAllEmployees());
    }

    /** L: total de sueldos del mes (para el dashboard). */
    @GetMapping("/total")
    public BigDecimal getMonthlyTotal() {
        return employeeService.getMonthlyTotal();
    }

    @GetMapping("/{id}")
    public EmployeeDTO getById(@PathVariable Integer id) {
        return employeeMapper.toDto(employeeService.getEmployeeById(id));
    }

    /** Actualiza sueldo y horas mensuales. */
    @PutMapping("/{id}")
    public EmployeeDTO update(@PathVariable Integer id,
                              @Valid @RequestBody EmployeeDTO dto) {
        employeeService.updateEmployeeSalary(id, dto.monthlySalary());
        var updated = employeeService.updateEmployeeHours(id, dto.monthlyHours());
        return employeeMapper.toDto(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }
}
