package com.bakery.infrastructure.persistence.jpa;

import com.bakery.infrastructure.persistence.entity.RecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecipeJpaRepository extends JpaRepository<RecipeEntity, Integer> {

    Optional<RecipeEntity> findByNameAndUserId(String name, Integer userId);

    List<RecipeEntity> findByUserId(Integer userId);

    Optional<RecipeEntity> findByIdAndUserId(Integer id, Integer userId);
}
