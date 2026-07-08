package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.FixedCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FixedCostJpaRepository extends JpaRepository<FixedCostEntity, Integer> {

    Optional<FixedCostEntity> findByName(String name);
}
