package com.bakery.application.port;

/**
 * Emisión de tokens de autenticación. La implementación (JWT) vive en
 * infraestructura de seguridad.
 */
public interface TokenService {

    /** Genera un token para el usuario dado. */
    String generateToken(Integer userId);
}
