package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.IInputRepository;
import com.bakery.domain.model.Input;
import com.bakery.infrastructure.persistence.entity.InputEntity;
import com.bakery.infrastructure.persistence.jpa.InputJpaRepository;
import com.bakery.infrastructure.persistence.mapper.InputEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia de insumos (scopeado por usuario).
 * Transaccional para que el mapeo recorra las relaciones LAZY dentro de la sesión.
 */
@Repository
@Transactional
public class InputRepositoryImpl implements IInputRepository {

    private final InputJpaRepository jpaRepository;
    private final InputEntityMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public InputRepositoryImpl(InputJpaRepository jpaRepository, InputEntityMapper mapper,
                               CurrentUserProvider currentUserProvider) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Input save(Input input) {
        InputEntity entity = mapper.toEntity(input);
        entity.setUserId(currentUserProvider.getCurrentUserId());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Input> findById(Integer id) {
        return jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Input> findAll() {
        return jpaRepository.findByUserId(currentUserProvider.getCurrentUserId())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .ifPresent(jpaRepository::delete);
    }
}
