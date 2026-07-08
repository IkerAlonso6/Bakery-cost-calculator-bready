package com.bakery.domain.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Define cómo se produce un lote: conjunto de ingredientes + rendimiento.
 * El rendimiento (yield) indica cuánto produce el lote (en kg o unidades)
 * y permite pasar del costo del lote al costo por unidad.
 */
public class Recipe {

    private static final MathContext DIVISION_CONTEXT = new MathContext(10);

    private final Integer id;
    private final String name;
    private final List<Ingredient> ingredients;
    private final BigDecimal yieldQuantity;
    private final UnitOfMeasurement yieldUnit;

    public Recipe(String name, BigDecimal yieldQuantity, UnitOfMeasurement yieldUnit) {
        this(null, name, yieldQuantity, yieldUnit);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public Recipe(Integer id, String name, BigDecimal yieldQuantity, UnitOfMeasurement yieldUnit) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Recipe name must not be blank");
        }
        if (yieldQuantity == null || yieldQuantity.signum() <= 0) {
            throw new IllegalArgumentException("Recipe yield quantity must be > 0");
        }
        if (yieldUnit == null) {
            throw new IllegalArgumentException("Recipe yield unit must not be null");
        }
        this.id = id;
        this.name = name.trim();
        this.yieldQuantity = yieldQuantity;
        this.yieldUnit = yieldUnit;
        this.ingredients = new ArrayList<>();
    }

    /**
     * Agrega un ingrediente. Un mismo insumo no puede repetirse en la receta.
     */
    public void addIngredient(Ingredient ingredient) {
        if (ingredient == null) {
            throw new IllegalArgumentException("Ingredient must not be null");
        }
        boolean duplicated = ingredients.stream()
                .anyMatch(existing -> sameInput(existing.getInput(), ingredient.getInput()));
        if (duplicated) {
            throw new IllegalArgumentException(
                    "Input '" + ingredient.getInput().getName() + "' is already in recipe '" + name + "'");
        }
        ingredients.add(ingredient);
    }

    private static boolean sameInput(Input a, Input b) {
        if (a.getId() != null && b.getId() != null) {
            return a.getId().equals(b.getId());
        }
        return a.getName().equalsIgnoreCase(b.getName());
    }

    public List<Ingredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    /** Costo de materiales del lote completo: suma de los costos de sus ingredientes. */
    public BigDecimal calculateTotalCost() {
        return ingredients.stream()
                .map(Ingredient::calculateCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Costo de materiales por unidad de rendimiento (kg o unidad producida). */
    public BigDecimal calculateCostPerUnit() {
        return calculateTotalCost().divide(yieldQuantity, DIVISION_CONTEXT);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getYieldQuantity() {
        return yieldQuantity;
    }

    public UnitOfMeasurement getYieldUnit() {
        return yieldUnit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe other)) return false;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Recipe{id=" + id + ", name='" + name + "', ingredients=" + ingredients.size()
                + ", yield=" + yieldQuantity + yieldUnit.getSymbol() + "}";
    }
}
