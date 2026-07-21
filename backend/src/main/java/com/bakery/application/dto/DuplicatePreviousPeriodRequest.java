package com.bakery.application.dto;

import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Body de POST .../duplicate-previous-period: copia los ítems de fromPeriod
 * (ej. "2026-06") hacia toPeriod (ej. "2026-07").
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DuplicatePreviousPeriodRequest {
    @NotBlank
    private String fromPeriod;
    @NotBlank
    private String toPeriod;
}
