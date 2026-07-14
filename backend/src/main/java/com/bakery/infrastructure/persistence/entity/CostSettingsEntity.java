package com.bakery.infrastructure.persistence.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import java.math.BigDecimal;

/**
 * Configuración de costeo. Una fila por usuario (user_id UNIQUE).
 */
@Entity
@Table(name = "cost_settings")
public class CostSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Integer userId;

    @Column(name = "default_target_margin", nullable = false, precision = 5, scale = 4)
    private BigDecimal defaultTargetMargin;

    @Column(name = "monthly_material_base", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlyMaterialBase;

    @Column(name = "currency", nullable = false, length = 3)
    private String currency;

    protected CostSettingsEntity() {
        // requerido por JPA
    }

    public CostSettingsEntity(Integer userId, BigDecimal defaultTargetMargin,
                              BigDecimal monthlyMaterialBase, String currency) {
        this.userId = userId;
        this.defaultTargetMargin = defaultTargetMargin;
        this.monthlyMaterialBase = monthlyMaterialBase;
        this.currency = currency;
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
