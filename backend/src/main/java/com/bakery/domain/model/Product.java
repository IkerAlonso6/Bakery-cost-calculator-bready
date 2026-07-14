package com.bakery.domain.model;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * Producto final que se vende. Siempre tiene una receta asociada.
 * - price: precio de venta manual (opcional; puede no estar fijado aún).
 * - targetMargin: override del margen objetivo global (opcional; null = usa el global).
 *
 * El costo total y el precio sugerido NO se calculan acá: requieren los
 * parámetros del negocio (costos fijos, sueldos, settings) -> CostingService.
 */
public class Product {

    private final Integer id;
    private final String name;
    private final Recipe recipe;
    private BigDecimal price;         // nullable
    private BigDecimal targetMargin;  // nullable, en [0, 1)

    public Product(String name, Recipe recipe) {
        this(null, name, recipe, null, null);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public Product(Integer id, String name, Recipe recipe, BigDecimal price, BigDecimal targetMargin) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name must not be blank");
        }
        if (recipe == null) {
            throw new IllegalArgumentException("Product recipe must not be null");
        }
        if (price != null) {
            validatePrice(price);
        }
        if (targetMargin != null) {
            validateMargin(targetMargin);
        }
        this.id = id;
        this.name = name.trim();
        this.recipe = recipe;
        this.price = price;
        this.targetMargin = targetMargin;
    }

    public void updatePrice(BigDecimal newPrice) {
        validatePrice(newPrice);
        this.price = newPrice;
    }

    /** Define el margen objetivo propio del producto. Pasar null para volver al global. */
    public void updateTargetMargin(BigDecimal newTargetMargin) {
        if (newTargetMargin != null) {
            validateMargin(newTargetMargin);
        }
        this.targetMargin = newTargetMargin;
    }

    /** Costo de materiales por unidad (delega en la receta). */
    public BigDecimal getMaterialCostPerUnit() {
        return recipe.calculateCostPerUnit();
    }

    private static void validatePrice(BigDecimal price) {
        if (price == null || price.signum() < 0) {
            throw new IllegalArgumentException("Product price must be >= 0");
        }
    }

    private static void validateMargin(BigDecimal margin) {
        if (margin.signum() < 0 || margin.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalArgumentException("Product target margin must be in [0, 1)");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    /** Precio de venta manual; vacío si aún no fue fijado. */
    public Optional<BigDecimal> getPrice() {
        return Optional.ofNullable(price);
    }

    /** Margen objetivo propio; vacío si usa el margen global del negocio. */
    public Optional<BigDecimal> getTargetMargin() {
        return Optional.ofNullable(targetMargin);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product)) return false;
        Product other = (Product) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Product{id=" + id + ", name='" + name + "', recipe=" + recipe.getName()
                + ", price=" + price + ", targetMargin=" + targetMargin + "}";
    }
}
