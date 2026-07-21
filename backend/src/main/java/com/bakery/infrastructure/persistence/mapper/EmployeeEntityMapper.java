package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.Employee;
import com.bakery.domain.model.EmployeeCategory;
import com.bakery.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

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
                employee.getMonthlyHours().orElse(null),
                employee.getCategory().name(),
                employee.getPeriod().atDay(1)
        );
    }

    public Employee toDomain(EmployeeEntity entity) {
        return new Employee(
                entity.getId(),
                entity.getName(),
                entity.getMonthlySalary(),
                entity.getMonthlyHours(),
                EmployeeCategory.valueOf(entity.getCategory()),
                YearMonth.from(entity.getPeriod())
        );
    }
}
