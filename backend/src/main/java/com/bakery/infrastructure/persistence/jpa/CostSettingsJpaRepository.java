package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.CostSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CostSettingsJpaRepository extends JpaRepository<CostSettingsEntity, Integer> {

    Optional<CostSettingsEntity> findByUserId(Integer userId);
}
