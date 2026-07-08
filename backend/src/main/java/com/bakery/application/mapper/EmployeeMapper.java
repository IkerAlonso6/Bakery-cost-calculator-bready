package com.bakery.application.mapper;

import com.bakery.application.dto.EmployeeDTO;
import com.bakery.domain.model.Employee;
import org.springframework.stereotype.Component;

import java.math.RoundingMode;
import java.util.List;

/**
 * Convierte Employee (dominio) <-> EmployeeDTO.
 * costPerHour se calcula en la respuesta (métrica informativa).
 */
@Component
public class EmployeeMapper {

    public EmployeeDTO toDto(Employee employee) {
        return new EmployeeDTO(
                employee.getId(),
                employee.getName(),
                employee.getMonthlySalary(),
                employee.getMonthlyHours().orElse(null),
                employee.costPerHour()
                        .map(v -> v.setScale(2, RoundingMode.HALF_UP))
                        .orElse(null)
        );
    }

    public List<EmployeeDTO> toDtoList(List<Employee> employees) {
        return employees.stream().map(this::toDto).toList();
    }

    public Employee toDomain(EmployeeDTO dto) {
        return new Employee(dto.name(), dto.monthlySalary(), dto.monthlyHours());
    }
}
