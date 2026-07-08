package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.IRecipeRepository;
import com.bakery.domain.model.Recipe;
import com.bakery.infrastructure.persistence.jpa.RecipeJpaRepository;
import com.bakery.infrastructure.persistence.mapper.RecipeEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia de recetas (agregado con ingredientes en cascada).
 */
@Repository
@Transactional
public class RecipeRepositoryImpl implements IRecipeRepository {

    private final RecipeJpaRepository jpaRepository;
    private final RecipeEntityMapper mapper;

    public RecipeRepositoryImpl(RecipeJpaRepository jpaRepository, RecipeEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Recipe save(Recipe recipe) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(recipe)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recipe> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recipe> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}
