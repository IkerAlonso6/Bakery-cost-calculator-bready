package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.FixedCost;
import com.bakery.domain.model.FixedCostCategory;
import com.bakery.infrastructure.persistence.entity.FixedCostEntity;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

/**
 * Convierte FixedCost (dominio) <-> FixedCostEntity.
 */
@Component
public class FixedCostEntityMapper {

    public FixedCostEntity toEntity(FixedCost fixedCost) {
        return new FixedCostEntity(
                fixedCost.getId(),
                fixedCost.getName(),
                fixedCost.getMonthlyAmount(),
                fixedCost.getCategory().name(),
                fixedCost.getPeriod().atDay(1)
        );
    }

    public FixedCost toDomain(FixedCostEntity entity) {
        return new FixedCost(
                entity.getId(),
                entity.getName(),
                entity.getMonthlyAmount(),
                FixedCostCategory.valueOf(entity.getCategory()),
                YearMonth.from(entity.getPeriod())
        );
    }
}
