package com.bakery.application.mapper;

import com.bakery.application.dto.CostSettingsDTO;
import com.bakery.domain.model.CostSettings;
import org.springframework.stereotype.Component;

/**
 * Convierte CostSettings (dominio) <-> CostSettingsDTO.
 */
@Component
public class CostSettingsMapper {

    public CostSettingsDTO toDto(CostSettings settings) {
        return new CostSettingsDTO(
                settings.getDefaultTargetMargin(),
                settings.getMonthlyMaterialBase(),
                settings.getCurrency()
        );
    }

    public CostSettings toDomain(CostSettingsDTO dto) {
        return new CostSettings(
                dto.defaultTargetMargin(),
                dto.monthlyMaterialBase(),
                dto.currency()
        );
    }
}
