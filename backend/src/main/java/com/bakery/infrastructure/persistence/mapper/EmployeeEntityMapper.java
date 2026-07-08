package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.Employee;
import com.bakery.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte Employee (dominio) <-> EmployeeEntity.
 */
@Component
public class EmployeeEntityMapper {

    public EmployeeEntity toEntity(Employee employee) {
        return new EmployeeEntity(
                employee.getId(),
                employee.getName(),
                employee.getMonthlySalary(),
                employee.getMonthlyHours().orElse(null)
        );
    }

    public Employee toDomain(EmployeeEntity entity) {
        return new Employee(
                entity.getId(),
                entity.getName(),
                entity.getMonthlySalary(),
                entity.getMonthlyHours()
        );
    }
}
