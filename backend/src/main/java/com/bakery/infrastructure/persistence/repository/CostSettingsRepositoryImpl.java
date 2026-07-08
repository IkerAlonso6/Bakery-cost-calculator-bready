package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.ICostSettingsRepository;
import com.bakery.domain.model.CostSettings;
import com.bakery.infrastructure.persistence.entity.CostSettingsEntity;
import com.bakery.infrastructure.persistence.jpa.CostSettingsJpaRepository;
import com.bakery.infrastructure.persistence.mapper.CostSettingsEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Adaptador de persistencia de la configuración de costeo (fila única, id = 1).
 */
@Repository
@Transactional
public class CostSettingsRepositoryImpl implements ICostSettingsRepository {

    private final CostSettingsJpaRepository jpaRepository;
    private final CostSettingsEntityMapper mapper;

    public CostSettingsRepositoryImpl(CostSettingsJpaRepository jpaRepository,
                                      CostSettingsEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CostSettings> get() {
        return jpaRepository.findById(CostSettingsEntity.SINGLETON_ID)
                .map(mapper::toDomain);
    }

    @Override
    public CostSettings save(CostSettings settings) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(settings)));
    }
}
