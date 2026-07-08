package com.bakery.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

/**
 * Configuración global de costeo. Fila única con id = 1
 * (CHECK cost_settings_singleton en la BD).
 */
@Entity
@Table(name = "cost_settings")
public class CostSettingsEntity {

    public static final short SINGLETON_ID = 1;

    @Id
    private Short id = SINGLETON_ID;

    @Column(name = "default_target_margin", nullable = false, precision = 5, scale = 4)
    private BigDecimal defaultTargetMargin;

    @Column(name = "monthly_material_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyMaterialBase;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    protected CostSettingsEntity() {
        // requerido por JPA
    }

    public CostSettingsEntity(BigDecimal defaultTargetMargin, BigDecimal monthlyMaterialBase, String currency) {
        this.id = SINGLETON_ID;
        this.defaultTargetMargin = defaultTargetMargin;
        this.monthlyMaterialBase = monthlyMaterialBase;
        this.currency = currency;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public BigDecimal getDefaultTargetMargin() {
        return defaultTargetMargin;
    }

    public void setDefaultTargetMargin(BigDecimal defaultTargetMargin) {
        this.defaultTargetMargin = defaultTargetMargin;
    }

    public BigDecimal getMonthlyMaterialBase() {
        return monthlyMaterialBase;
    }

    public void setMonthlyMaterialBase(BigDecimal monthlyMaterialBase) {
        this.monthlyMaterialBase = monthlyMaterialBase;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
