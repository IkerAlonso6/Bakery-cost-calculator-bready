package com.bakery.infrastructure.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "yield_quantity", nullable = false, precision = 10, scale = 4)
    private BigDecimal yieldQuantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "yield_unit_id", nullable = false)
    private UnitOfMeasurementEntity yieldUnit;

    // Entidad débil: los ingredientes viven y mueren con la receta
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<IngredientEntity> ingredients = new ArrayList<>();

    protected RecipeEntity() {
        // requerido por JPA
    }

    public RecipeEntity(Integer id, String name, BigDecimal yieldQuantity, UnitOfMeasurementEntity yieldUnit) {
        this.id = id;
        this.name = name;
        this.yieldQuantity = yieldQuantity;
        this.yieldUnit = yieldUnit;
    }

    /** Mantiene sincronizados ambos lados de la relación. */
    public void addIngredient(IngredientEntity ingredient) {
        ingredient.setRecipe(this);
        this.ingredients.add(ingredient);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getYieldQuantity() {
        return yieldQuantity;
    }

    public void setYieldQuantity(BigDecimal yieldQuantity) {
        this.yieldQuantity = yieldQuantity;
    }

    public UnitOfMeasurementEntity getYieldUnit() {
        return yieldUnit;
    }

    public void setYieldUnit(UnitOfMeasurementEntity yieldUnit) {
        this.yieldUnit = yieldUnit;
    }

    public List<IngredientEntity> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<IngredientEntity> ingredients) {
        this.ingredients = ingredients;
    }
}
