package com.bakery.application.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de insumo. unitOfMeasure es el nombre del enum (KILOGRAM, GRAM...).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InputDTO {
    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String unitOfMeasure;
    @NotNull @Positive
    private BigDecimal price;
}
