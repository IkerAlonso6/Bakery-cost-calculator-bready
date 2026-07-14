package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.CostSettings;
import com.bakery.infrastructure.persistence.entity.CostSettingsEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte CostSettingsEntity -> CostSettings (dominio).
 * La construcción de la entidad (con user_id) la maneja el RepositoryImpl.
 */
@Component
public class CostSettingsEntityMapper {

    public CostSettings toDomain(CostSettingsEntity entity) {
        return new CostSettings(
                entity.getDefaultTargetMargin(),
                entity.getMonthlyMaterialBase(),
                entity.getCurrency()
        );
    }
}
