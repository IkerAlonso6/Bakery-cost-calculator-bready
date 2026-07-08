package com.bakery.domain.model;

/**
 * Unidades de medida soportadas para insumos y rendimientos de receta.
 * Corresponden 1:1 con la tabla units_of_measurement (ids 1-6).
 */
public enum UnitOfMeasurement {

    KILOGRAM("kg"),
    GRAM("g"),
    MILLIGRAM("mg"),
    LITER("l"),
    MILLILITER("ml"),
    UNIT("u");

    private final String symbol;

    UnitOfMeasurement(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
