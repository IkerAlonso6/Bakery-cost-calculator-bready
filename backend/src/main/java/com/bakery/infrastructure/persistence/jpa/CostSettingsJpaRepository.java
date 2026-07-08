package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.CostSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CostSettingsJpaRepository extends JpaRepository<CostSettingsEntity, Short> {
}
