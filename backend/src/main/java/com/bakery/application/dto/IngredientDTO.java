package com.bakery.application.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de ingrediente (uso de un insumo en una receta).
 * inputName y cost son de solo lectura: los completa el mapper en las respuestas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private Integer id;
    @NotNull
    private Integer inputId;
    private String inputName;
    @NotNull @Positive
    private BigDecimal quantity;
    private BigDecimal cost;
}
