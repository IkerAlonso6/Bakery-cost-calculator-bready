package com.bakery.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

/**
 * Body de los endpoints PUT .../price.
 */
public record UpdatePriceRequest(
        @NotNull @PositiveOrZero BigDecimal price
) {}
