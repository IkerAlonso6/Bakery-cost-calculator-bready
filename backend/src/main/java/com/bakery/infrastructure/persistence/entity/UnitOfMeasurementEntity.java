package com.bakery.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Tabla de referencia units_of_measurement (ids fijos 1-6, seed de V1__init.sql).
 */
@Entity
@Table(name = "units_of_measurement")
public class UnitOfMeasurementEntity {

    @Id
    private Short id;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    protected UnitOfMeasurementEntity() {
        // requerido por JPA
    }

    public UnitOfMeasurementEntity(Short id, String name) {
        this.id = id;
        this.name = name;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
