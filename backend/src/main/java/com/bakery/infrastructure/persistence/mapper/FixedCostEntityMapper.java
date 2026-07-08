package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.FixedCost;
import com.bakery.infrastructure.persistence.entity.FixedCostEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte FixedCost (dominio) <-> FixedCostEntity.
 */
@Component
public class FixedCostEntityMapper {

    public FixedCostEntity toEntity(FixedCost fixedCost) {
        return new FixedCostEntity(
                fixedCost.getId(),
                fixedCost.getName(),
                fixedCost.getMonthlyAmount()
        );
    }

    public FixedCost toDomain(FixedCostEntity entity) {
        return new FixedCost(
                entity.getId(),
                entity.getName(),
                entity.getMonthlyAmount()
        );
    }
}
