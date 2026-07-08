package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.CostSettings;
import com.bakery.infrastructure.persistence.entity.CostSettingsEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte CostSettings (dominio) <-> CostSettingsEntity (singleton).
 */
@Component
public class CostSettingsEntityMapper {

    public CostSettingsEntity toEntity(CostSettings settings) {
        return new CostSettingsEntity(
                settings.getDefaultTargetMargin(),
                settings.getMonthlyMaterialBase(),
                settings.getCurrency()
        );
    }

    public CostSettings toDomain(CostSettingsEntity entity) {
        return new CostSettings(
                entity.getDefaultTargetMargin(),
                entity.getMonthlyMaterialBase(),
                entity.getCurrency()
        );
    }
}
