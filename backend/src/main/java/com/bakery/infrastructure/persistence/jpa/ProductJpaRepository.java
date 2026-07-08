package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {

    Optional<ProductEntity> findByName(String name);
}
