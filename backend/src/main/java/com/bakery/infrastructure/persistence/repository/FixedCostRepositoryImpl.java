package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.IFixedCostRepository;
import com.bakery.domain.model.FixedCost;
import com.bakery.infrastructure.persistence.entity.FixedCostEntity;
import com.bakery.infrastructure.persistence.jpa.FixedCostJpaRepository;
import com.bakery.infrastructure.persistence.mapper.FixedCostEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia de costos fijos (scopeado por usuario).
 */
@Repository
@Transactional
public class FixedCostRepositoryImpl implements IFixedCostRepository {

    private final FixedCostJpaRepository jpaRepository;
    private final FixedCostEntityMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public FixedCostRepositoryImpl(FixedCostJpaRepository jpaRepository, FixedCostEntityMapper mapper,
                                   CurrentUserProvider currentUserProvider) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public FixedCost save(FixedCost fixedCost) {
        FixedCostEntity entity = mapper.toEntity(fixedCost);
        entity.setUserId(currentUserProvider.getCurrentUserId());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<FixedCost> findById(Integer id) {
        return jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixedCost> findByPeriod(YearMonth period) {
        return jpaRepository.findByUserIdAndPeriod(currentUserProvider.getCurrentUserId(), period.atDay(1))
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<YearMonth> findMostRecentPeriodWithDataUpTo(YearMonth period) {
        return jpaRepository.findMostRecentPeriodWithDataUpTo(currentUserProvider.getCurrentUserId(), period.atDay(1))
                .map(YearMonth::from);
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .ifPresent(jpaRepository::delete);
    }
}
