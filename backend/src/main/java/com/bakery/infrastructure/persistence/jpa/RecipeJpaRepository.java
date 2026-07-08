package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipeJpaRepository extends JpaRepository<RecipeEntity, Integer> {

    Optional<RecipeEntity> findByName(String name);
}
