package com.bakery.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price; // nullable: puede no estar fijado

    @Column(name = "target_margin", precision = 5, scale = 4)
    private BigDecimal targetMargin; // nullable: usa el global

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private RecipeEntity recipe;

    protected ProductEntity() {
        // requerido por JPA
    }

    public ProductEntity(Integer id, String name, BigDecimal price, BigDecimal targetMargin, RecipeEntity recipe) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.targetMargin = targetMargin;
        this.recipe = recipe;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getTargetMargin() {
        return targetMargin;
    }

    public void setTargetMargin(BigDecimal targetMargin) {
        this.targetMargin = targetMargin;
    }

    public RecipeEntity getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeEntity recipe) {
        this.recipe = recipe;
    }
}
