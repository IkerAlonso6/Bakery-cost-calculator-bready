package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.User;
import com.bakery.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte User (dominio) <-> UserEntity. La foto se maneja aparte.
 */
@Component
public class UserEntityMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.getId(),
                user.getEmail(),
                user.getPasswordHash(),
                user.getDisplayName()
        );
    }

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getDisplayName()
        );
    }
}
