package com.bakery.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Insumo / materia prima que se compra (harina, manteca, levadura...).
 * El precio corresponde a una unidad de su unidad de medida.
 */
public class Input {

    private final Integer id;
    private final String name;
    private final UnitOfMeasurement unitOfMeasure;
    private BigDecimal price;

    public Input(String name, UnitOfMeasurement unitOfMeasure, BigDecimal price) {
        this(null, name, unitOfMeasure, price);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public Input(Integer id, String name, UnitOfMeasurement unitOfMeasure, BigDecimal price) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Input name must not be blank");
        }
        if (unitOfMeasure == null) {
            throw new IllegalArgumentException("Input unit of measure must not be null");
        }
        validatePrice(price);
        this.id = id;
        this.name = name.trim();
        this.unitOfMeasure = unitOfMeasure;
        this.price = price;
    }

    public void updatePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Input price must be >= 0");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public UnitOfMeasurement getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public BigDecimal getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Input)) return false;
        Input other = (Input) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Input{id=" + id + ", name='" + name + "', unit=" + unitOfMeasure.getSymbol()
                + ", price=" + price + "}";
    }
}
