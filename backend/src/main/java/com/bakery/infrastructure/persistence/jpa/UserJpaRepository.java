package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    /** Indica si el usuario tiene foto sin traer los bytes del blob. */
    @Query("SELECT (u.photo IS NOT NULL) FROM UserEntity u WHERE u.id = :id")
    Optional<Boolean> photoPresence(@Param("id") Integer id);
}
