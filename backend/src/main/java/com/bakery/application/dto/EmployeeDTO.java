package com.bakery.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * DTO de empleado. monthlyHours es opcional; costPerHour es de solo
 * lectura (métrica informativa que completa el mapper).
 */
public record EmployeeDTO(
        Integer id,
        @NotBlank String name,
        @NotNull @PositiveOrZero BigDecimal monthlySalary,
        @Positive BigDecimal monthlyHours,
        BigDecimal costPerHour
) {}
