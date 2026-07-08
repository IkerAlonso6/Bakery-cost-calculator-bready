package com.bakery.application.port;

import com.bakery.domain.model.CostSettings;

import java.util.Optional;

/**
 * Port de persistencia de la configuración de costeo (fila única).
 */
public interface ICostSettingsRepository {

    /** Devuelve la configuración global si ya fue inicializada. */
    Optional<CostSettings> get();

    CostSettings save(CostSettings settings);
}
