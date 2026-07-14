package com.bakery.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Costo fijo mensual itemizado (gas, agua, luz, alquiler...).
 * La suma de todos los FixedCost del negocio es F en COSTING_MODEL.md.
 */
public class FixedCost {

    private final Integer id;
    private final String name;
    private BigDecimal monthlyAmount;

    public FixedCost(String name, BigDecimal monthlyAmount) {
        this(null, name, monthlyAmount);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public FixedCost(Integer id, String name, BigDecimal monthlyAmount) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("FixedCost name must not be blank");
        }
        validateAmount(monthlyAmount);
        this.id = id;
        this.name = name.trim();
        this.monthlyAmount = monthlyAmount;
    }

    public void updateMonthlyAmount(BigDecimal newAmount) {
        validateAmount(newAmount);
        this.monthlyAmount = newAmount;
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
        return "FixedCost{id=" + id + ", name='" + name + "', monthlyAmount=" + monthlyAmount + "}";
    }
}
