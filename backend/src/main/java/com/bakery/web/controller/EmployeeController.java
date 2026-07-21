package com.bakery.web.controller;

import com.bakery.application.dto.DuplicatePreviousPeriodRequest;
import com.bakery.application.dto.EmployeeDTO;
import com.bakery.application.mapper.EmployeeMapper;
import com.bakery.application.service.EmployeeService;
import com.bakery.domain.model.EmployeeCategory;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
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
        var created = employeeService.createEmployee(
                dto.getName(), dto.getMonthlySalary(), dto.getMonthlyHours(),
                EmployeeCategory.valueOf(dto.getCategory()), YearMonth.parse(dto.getPeriod()));
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeMapper.toDto(created));
    }

    /** Lista los empleados del período dado (por defecto, el mes actual). */
    @GetMapping
    public List<EmployeeDTO> getAll(@RequestParam(required = false) String period) {
        return employeeMapper.toDtoList(employeeService.getEmployeesForPeriod(resolvePeriod(period)));
    }

    /** L: total de sueldos del período (por defecto, el mes actual). Para el dashboard. */
    @GetMapping("/total")
    public BigDecimal getMonthlyTotal(@RequestParam(required = false) String period) {
        return employeeService.getMonthlyTotal(resolvePeriod(period));
    }

    @GetMapping("/{id}")
    public EmployeeDTO getById(@PathVariable Integer id) {
        return employeeMapper.toDto(employeeService.getEmployeeById(id));
    }

    /** Actualiza sueldo, horas y categoría. El período de una fila no se edita. */
    @PutMapping("/{id}")
    public EmployeeDTO update(@PathVariable Integer id,
                              @Valid @RequestBody EmployeeDTO dto) {
        employeeService.updateEmployeeSalary(id, dto.getMonthlySalary());
        employeeService.updateEmployeeHours(id, dto.getMonthlyHours());
        var updated = employeeService.updateEmployeeCategory(id, EmployeeCategory.valueOf(dto.getCategory()));
        return employeeMapper.toDto(updated);
    }

    /** Copia los empleados de fromPeriod hacia toPeriod (omite nombres ya existentes en toPeriod). */
    @PostMapping("/duplicate-previous-period")
    public List<EmployeeDTO> duplicatePreviousPeriod(@Valid @RequestBody DuplicatePreviousPeriodRequest request) {
        var created = employeeService.duplicateFromPreviousPeriod(
                YearMonth.parse(request.getFromPeriod()), YearMonth.parse(request.getToPeriod()));
        return employeeMapper.toDtoList(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    private static YearMonth resolvePeriod(String period) {
        return period != null ? YearMonth.parse(period) : YearMonth.now();
    }
}
