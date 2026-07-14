package com.bakery.application.mapper;

import com.bakery.application.dto.UserDTO;
import com.bakery.domain.model.User;
import org.springframework.stereotype.Component;

/**
 * Convierte User (dominio) -> UserDTO. Nunca expone el hash de contraseña.
 * El flag hasPhoto lo provee la capa de persistencia.
 */
@Component
public class UserMapper {

    public UserDTO toDto(User user, boolean hasPhoto) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                hasPhoto
        );
    }
}
