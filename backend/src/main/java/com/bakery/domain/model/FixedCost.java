package com.bakery.domain.model;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Objects;

/**
 * Costo fijo mensual itemizado (gas, agua, luz, alquiler...).
 * La suma de los FixedCost del período es F en COSTING_MODEL.md.
 * category es puramente organizativa (no cambia el cálculo de costeo).
 * period identifica el mes al que pertenece esta fila (no editable post-creación).
 */
public class FixedCost {

    private final Integer id;
    private final String name;
    private BigDecimal monthlyAmount;
    private FixedCostCategory category;
    private final YearMonth period;

    public FixedCost(String name, BigDecimal monthlyAmount, FixedCostCategory category, YearMonth period) {
        this(null, name, monthlyAmount, category, period);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public FixedCost(Integer id, String name, BigDecimal monthlyAmount, FixedCostCategory category, YearMonth period) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("FixedCost name must not be blank");
        }
        validateAmount(monthlyAmount);
        if (category == null) {
            throw new IllegalArgumentException("FixedCost category must not be null");
        }
        if (period == null) {
            throw new IllegalArgumentException("FixedCost period must not be null");
        }
        this.id = id;
        this.name = name.trim();
        this.monthlyAmount = monthlyAmount;
        this.category = category;
        this.period = period;
    }

    public void updateMonthlyAmount(BigDecimal newAmount) {
        validateAmount(newAmount);
        this.monthlyAmount = newAmount;
    }

    public void updateCategory(FixedCostCategory newCategory) {
        if (newCategory == null) {
            throw new IllegalArgumentException("FixedCost category must not be null");
        }
        this.category = newCategory;
    }

    private static void validateAmount(BigDecimal amount) {
        if (amount == null || amount.signum() < 0) {
            throw new IllegalArgumentException("FixedCost monthly amount must be >= 0");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public FixedCostCategory getCategory() {
        return category;
    }

    public YearMonth getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixedCost)) return false;
        FixedCost other = (FixedCost) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FixedCost{id=" + id + ", name='" + name + "', monthlyAmount=" + monthlyAmount
                + ", category=" + category + ", period=" + period + "}";
    }
}
