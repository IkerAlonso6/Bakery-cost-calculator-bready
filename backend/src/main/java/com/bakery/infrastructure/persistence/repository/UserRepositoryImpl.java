package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.IUserRepository;
import com.bakery.domain.model.User;
import com.bakery.infrastructure.persistence.entity.UserEntity;
import com.bakery.infrastructure.persistence.jpa.UserJpaRepository;
import com.bakery.infrastructure.persistence.mapper.UserEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adaptador de persistencia de usuarios. Preserva la foto en las
 * actualizaciones (el dominio no la transporta).
 */
@Repository
@Transactional
public class UserRepositoryImpl implements IUserRepository {

    private final UserJpaRepository jpaRepository;
    private final UserEntityMapper mapper;

    public UserRepositoryImpl(UserJpaRepository jpaRepository, UserEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public User save(User user) {
        UserEntity entity;
        if (user.getId() != null) {
            entity = jpaRepository.findById(user.getId()).orElseGet(() -> mapper.toEntity(user));
            entity.setEmail(user.getEmail());
            entity.setPasswordHash(user.getPasswordHash());
            entity.setDisplayName(user.getDisplayName());
        } else {
            entity = mapper.toEntity(user);
        }
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPhoto(Integer userId) {
        return jpaRepository.photoPresence(userId).orElse(false);
    }

    @Override
    public void updatePhoto(Integer userId, byte[] photo, String contentType) {
        UserEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));
        entity.setPhoto(photo);
        entity.setPhotoContentType(contentType);
        jpaRepository.save(entity);
    }

    @Override
    public void deletePhoto(Integer userId) {
        UserEntity entity = jpaRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("User not found with id: " + userId));
        entity.setPhoto(null);
        entity.setPhotoContentType(null);
        jpaRepository.save(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<StoredPhoto> findPhoto(Integer userId) {
        return jpaRepository.findById(userId)
                .filter(e -> e.getPhoto() != null && e.getPhoto().length > 0)
                .map(e -> new StoredPhoto(e.getPhoto(), e.getPhotoContentType()));
    }
}
