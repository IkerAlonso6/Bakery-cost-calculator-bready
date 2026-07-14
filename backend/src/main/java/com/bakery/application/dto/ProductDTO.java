package com.bakery.application.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de producto. price y targetMargin son opcionales:
 * - price null = todavía sin precio manual fijado.
 * - targetMargin null = usa el margen global de cost_settings.
 * recipeName es de solo lectura (lo completa el mapper).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Integer id;
    @NotBlank
    private String name;
    @NotNull
    private Integer recipeId;
    private String recipeName;
    @PositiveOrZero
    private BigDecimal price;
    @DecimalMin("0.0") @DecimalMax(value = "1.0", inclusive = false)
    private BigDecimal targetMargin;
}
