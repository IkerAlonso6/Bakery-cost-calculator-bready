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
 * lectura (métrica informativa que completa el mapper). category es un
 * valor fijo (ver EmployeeCategory); period es "yyyy-MM" y no se edita
 * post-creación (ver EmployeeMapper).
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
    @NotBlank
    private String category;
    @NotBlank
    private String period;
    private BigDecimal costPerHour;
}
