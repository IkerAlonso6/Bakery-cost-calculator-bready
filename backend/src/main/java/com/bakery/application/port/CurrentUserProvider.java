package com.bakery.application.port;

/**
 * Abstracción para obtener el usuario autenticado en la petición actual.
 * La implementación (infraestructura de seguridad) lo resuelve desde el
 * contexto de seguridad; la capa de aplicación/persistencia sólo depende
 * de esta interfaz para hacer scoping multi-tenant.
 */
public interface CurrentUserProvider {

    /**
     * @return id del usuario autenticado.
     * @throws IllegalStateException si no hay usuario autenticado.
     */
    Integer getCurrentUserId();
}
