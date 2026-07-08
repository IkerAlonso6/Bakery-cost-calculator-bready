package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Integer> {
}
