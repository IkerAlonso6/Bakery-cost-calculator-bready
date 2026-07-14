package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.InputEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InputJpaRepository extends JpaRepository<InputEntity, Integer> {

    Optional<InputEntity> findByNameAndUserId(String name, Integer userId);

    List<InputEntity> findByUserId(Integer userId);

    Optional<InputEntity> findByIdAndUserId(Integer id, Integer userId);
}
