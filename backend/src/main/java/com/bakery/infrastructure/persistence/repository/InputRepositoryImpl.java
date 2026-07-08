package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.IInputRepository;
import com.bakery.domain.model.Input;
import com.bakery.infrastructure.persistence.jpa.InputJpaRepository;
import com.bakery.infrastructure.persistence.mapper.InputEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia de insumos: implementa el port usando
 * Spring Data + mapper entity<->dominio.
 * Transaccional para que el mapeo recorra las relaciones LAZY dentro de la sesión.
 */
@Repository
@Transactional
public class InputRepositoryImpl implements IInputRepository {

    private final InputJpaRepository jpaRepository;
    private final InputEntityMapper mapper;

    public InputRepositoryImpl(InputJpaRepository jpaRepository, InputEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Input save(Input input) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(input)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Input> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Input> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}
