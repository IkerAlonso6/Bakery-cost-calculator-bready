package com.bakery.domain.model;

/**
 * Categorías fijas para clasificar costos fijos/overhead (organizativo,
 * no afecta el cálculo de costeo — ver CostingService).
 */
public enum FixedCostCategory {
    ALQUILER,
    SERVICIOS,
    MANTENIMIENTO,
    MARKETING,
    IMPUESTOS_SEGUROS,
    OTROS
}
