package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Integer> {

    Optional<ProductEntity> findByNameAndUserId(String name, Integer userId);

    List<ProductEntity> findByUserId(Integer userId);

    Optional<ProductEntity> findByIdAndUserId(Integer id, Integer userId);
}
