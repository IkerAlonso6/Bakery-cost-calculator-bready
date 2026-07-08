package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.IFixedCostRepository;
import com.bakery.domain.model.FixedCost;
import com.bakery.infrastructure.persistence.jpa.FixedCostJpaRepository;
import com.bakery.infrastructure.persistence.mapper.FixedCostEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia de costos fijos.
 */
@Repository
@Transactional
public class FixedCostRepositoryImpl implements IFixedCostRepository {

    private final FixedCostJpaRepository jpaRepository;
    private final FixedCostEntityMapper mapper;

    public FixedCostRepositoryImpl(FixedCostJpaRepository jpaRepository, FixedCostEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public FixedCost save(FixedCost fixedCost) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(fixedCost)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FixedCost> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixedCost> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}
