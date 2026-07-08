package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.UnitOfMeasurement;
import com.bakery.infrastructure.persistence.entity.UnitOfMeasurementEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte el enum de dominio <-> la tabla de referencia units_of_measurement.
 * Los ids son fijos (1-6) y corresponden al orden del enum (seed de V1__init.sql).
 */
@Component
public class UnitOfMeasurementEntityMapper {

    public UnitOfMeasurementEntity toEntity(UnitOfMeasurement unit) {
        return new UnitOfMeasurementEntity((short) (unit.ordinal() + 1), unit.name());
    }

    public UnitOfMeasurement toDomain(UnitOfMeasurementEntity entity) {
        return UnitOfMeasurement.valueOf(entity.getName());
    }
}
