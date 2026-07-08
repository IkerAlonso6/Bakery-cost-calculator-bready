package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.Input;
import com.bakery.infrastructure.persistence.entity.InputEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte Input (dominio) <-> InputEntity.
 */
@Component
public class InputEntityMapper {

    private final UnitOfMeasurementEntityMapper unitMapper;

    public InputEntityMapper(UnitOfMeasurementEntityMapper unitMapper) {
        this.unitMapper = unitMapper;
    }

    public InputEntity toEntity(Input input) {
        return new InputEntity(
                input.getId(),
                input.getName(),
                input.getPrice(),
                unitMapper.toEntity(input.getUnitOfMeasure())
        );
    }

    public Input toDomain(InputEntity entity) {
        return new Input(
                entity.getId(),
                entity.getName(),
                unitMapper.toDomain(entity.getUnitOfMeasurement()),
                entity.getPrice()
        );
    }
}
