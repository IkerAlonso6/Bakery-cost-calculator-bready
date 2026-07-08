package com.bakery.domain.model;

import java.math.BigDecimal;

/**
 * Uso de un insumo dentro de una receta, con una cantidad específica.
 * La cantidad se expresa en la misma unidad que el insumo.
 * Entidad débil: no existe fuera de una Recipe.
 */
public class Ingredient {

    private final Integer id;
    private final Input input;
    private final BigDecimal quantity;

    public Ingredient(Input input, BigDecimal quantity) {
        this(null, input, quantity);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public Ingredient(Integer id, Input input, BigDecimal quantity) {
        if (input == null) {
            throw new IllegalArgumentException("Ingredient input must not be null");
        }
        if (quantity == null || quantity.signum() <= 0) {
            throw new IllegalArgumentException("Ingredient quantity must be > 0");
        }
        this.id = id;
        this.input = input;
        this.quantity = quantity;
    }

    /** Costo del ingrediente: precio del insumo x cantidad usada. */
    public BigDecimal calculateCost() {
        return input.getPrice().multiply(quantity);
    }

    public Integer getId() {
        return id;
    }

    public Input getInput() {
        return input;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Ingredient{id=" + id + ", input=" + input.getName()
                + ", quantity=" + quantity + input.getUnitOfMeasure().getSymbol() + "}";
    }
}
