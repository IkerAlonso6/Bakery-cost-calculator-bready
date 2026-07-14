package com.bakery.application.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Body de los endpoints PUT .../price.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriceRequest {
    @NotNull @PositiveOrZero
    private BigDecimal price;
}
