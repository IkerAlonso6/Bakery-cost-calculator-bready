package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.FixedCostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FixedCostJpaRepository extends JpaRepository<FixedCostEntity, Integer> {

    Optional<FixedCostEntity> findByNameAndUserId(String name, Integer userId);

    List<FixedCostEntity> findByUserId(Integer userId);

    Optional<FixedCostEntity> findByIdAndUserId(Integer id, Integer userId);
}
