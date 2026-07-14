package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeJpaRepository extends JpaRepository<EmployeeEntity, Integer> {

    List<EmployeeEntity> findByUserId(Integer userId);

    Optional<EmployeeEntity> findByIdAndUserId(Integer id, Integer userId);
}
