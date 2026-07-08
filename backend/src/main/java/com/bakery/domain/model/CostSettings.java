package com.bakery.domain.model;

import java.math.BigDecimal;

/**
 * Parámetros globales de costeo del negocio (singleton en BD).
 * - defaultTargetMargin: margen objetivo global sobre el precio de venta, en [0, 1).
 * - monthlyMaterialBase: M, costo de materiales de un mes típico (base de absorción).
 * - currency: moneda de los importes.
 */
public class CostSettings {

    private BigDecimal defaultTargetMargin;
    private BigDecimal monthlyMaterialBase;
    private String currency;

    public CostSettings(BigDecimal defaultTargetMargin, BigDecimal monthlyMaterialBase, String currency) {
        validateMargin(defaultTargetMargin);
        validateMaterialBase(monthlyMaterialBase);
        validateCurrency(currency);
        this.defaultTargetMargin = defaultTargetMargin;
        this.monthlyMaterialBase = monthlyMaterialBase;
        this.currency = currency.trim();
    }

    public void updateDefaultTargetMargin(BigDecimal newMargin) {
        validateMargin(newMargin);
        this.defaultTargetMargin = newMargin;
    }

    public void updateMonthlyMaterialBase(BigDecimal newBase) {
        validateMaterialBase(newBase);
        this.monthlyMaterialBase = newBase;
    }

    public void updateCurrency(String newCurrency) {
        validateCurrency(newCurrency);
        this.currency = newCurrency.trim();
    }

    private static void validateMargin(BigDecimal margin) {
        if (margin == null || margin.signum() < 0 || margin.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalArgumentException("Default target margin must be in [0, 1)");
        }
    }

    private static void validateMaterialBase(BigDecimal base) {
        if (base == null || base.signum() <= 0) {
            throw new IllegalArgumentException("Monthly material base (M) must be > 0");
        }
    }

    private static void validateCurrency(String currency) {
        if (currency == null || currency.isBlank()) {
            throw new IllegalArgumentException("Currency must not be blank");
        }
    }

    public BigDecimal getDefaultTargetMargin() {
        return defaultTargetMargin;
    }

    public BigDecimal getMonthlyMaterialBase() {
        return monthlyMaterialBase;
    }

    public String getCurrency() {
        return currency;
    }

    @Override
    public String toString() {
        return "CostSettings{defaultTargetMargin=" + defaultTargetMargin
                + ", monthlyMaterialBase=" + monthlyMaterialBase
                + ", currency='" + currency + "'}";
    }
}
