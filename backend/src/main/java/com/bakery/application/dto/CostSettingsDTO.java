package com.bakery.application.dto;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de la configuración global de costeo (fila única).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostSettingsDTO {
    @NotNull @DecimalMin("0.0") @DecimalMax(value = "1.0", inclusive = false)
    private BigDecimal defaultTargetMargin;
    @NotNull @Positive
    private BigDecimal monthlyMaterialBase;
    @NotBlank
    private String currency;
}
