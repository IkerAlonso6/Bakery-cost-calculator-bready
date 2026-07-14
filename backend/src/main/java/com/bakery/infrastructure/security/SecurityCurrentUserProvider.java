package com.bakery.infrastructure.security;

import com.bakery.application.port.CurrentUserProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resuelve el usuario autenticado desde el contexto de seguridad de Spring.
 * El principal es el id del usuario (Integer), fijado por {@link JwtAuthenticationFilter}.
 */
@Component
public class SecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof Integer)) {
            throw new IllegalStateException("No authenticated user in the security context");
        }
        return (Integer) auth.getPrincipal();
    }
}
