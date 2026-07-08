package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.InputEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InputJpaRepository extends JpaRepository<InputEntity, Integer> {

    Optional<InputEntity> findByName(String name);
}
