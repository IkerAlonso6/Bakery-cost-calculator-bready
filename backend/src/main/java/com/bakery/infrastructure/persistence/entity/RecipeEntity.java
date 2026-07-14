package com.bakery.infrastructure.persistence.entity;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class RecipeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

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

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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
