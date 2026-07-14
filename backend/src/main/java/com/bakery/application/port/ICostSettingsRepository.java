package com.bakery.application.port;

import com.bakery.domain.model.CostSettings;

import java.util.Optional;

/**
 * Port de persistencia de la configuración de costeo (una fila por usuario).
 */
public interface ICostSettingsRepository {

    /** Devuelve la configuración del usuario actual si ya fue inicializada. */
    Optional<CostSettings> get();

    /** Guarda (upsert) la configuración del usuario actual. */
    CostSettings save(CostSettings settings);

    /**
     * Crea la configuración por defecto para un usuario recién registrado.
     * No depende del contexto de seguridad (el usuario aún no está autenticado).
     */
    void createDefaultFor(Integer userId);
}
