package com.bakery.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de empleado. monthlyHours es opcional; costPerHour es de solo
 * lectura (métrica informativa que completa el mapper).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDTO {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull @PositiveOrZero
    private BigDecimal monthlySalary;
    @Positive
    private BigDecimal monthlyHours;
    private BigDecimal costPerHour;
}
