package com.bakery.application.service;

import com.bakery.application.port.ICostSettingsRepository;
import com.bakery.domain.model.CostSettings;
import org.springframework.stereotype.Service;

/**
 * Casos de uso de la configuración global de costeo (fila única).
 */
@Service
public class CostSettingsService {

    private final ICostSettingsRepository costSettingsRepository;

    public CostSettingsService(ICostSettingsRepository costSettingsRepository) {
        this.costSettingsRepository = costSettingsRepository;
    }

    /**
     * Devuelve la configuración del negocio.
     * Lanza IllegalStateException si aún no fue configurada
     * (p. ej. monthly_material_base todavía en 0 en el seed).
     */
    public CostSettings getSettings() {
        return costSettingsRepository.get()
                .orElseThrow(() -> new IllegalStateException(
                        "Cost settings are not configured yet. Set the target margin and monthly material base first."));
    }

    public CostSettings updateSettings(CostSettings newSettings) {
        return costSettingsRepository.save(newSettings);
    }
}
