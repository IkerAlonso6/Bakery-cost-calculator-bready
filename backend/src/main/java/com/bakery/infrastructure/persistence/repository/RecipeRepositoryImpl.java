package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.IRecipeRepository;
import com.bakery.domain.model.Recipe;
import com.bakery.infrastructure.persistence.entity.RecipeEntity;
import com.bakery.infrastructure.persistence.jpa.RecipeJpaRepository;
import com.bakery.infrastructure.persistence.mapper.RecipeEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia de recetas (agregado con ingredientes en cascada,
 * scopeado por usuario).
 */
@Repository
@Transactional
public class RecipeRepositoryImpl implements IRecipeRepository {

    private final RecipeJpaRepository jpaRepository;
    private final RecipeEntityMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public RecipeRepositoryImpl(RecipeJpaRepository jpaRepository, RecipeEntityMapper mapper,
                                CurrentUserProvider currentUserProvider) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Recipe save(Recipe recipe) {
        RecipeEntity entity = mapper.toEntity(recipe);
        entity.setUserId(currentUserProvider.getCurrentUserId());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Recipe> findById(Integer id) {
        return jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Recipe> findAll() {
        return jpaRepository.findByUserId(currentUserProvider.getCurrentUserId())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .ifPresent(jpaRepository::delete);
    }
}
