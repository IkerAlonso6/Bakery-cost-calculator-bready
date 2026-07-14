package com.bakery.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de costo fijo mensual (gas, agua, luz, alquiler...).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixedCostDTO {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull @PositiveOrZero
    private BigDecimal monthlyAmount;
}
