package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.ICostSettingsRepository;
import com.bakery.domain.model.CostSettings;
import com.bakery.infrastructure.persistence.entity.CostSettingsEntity;
import com.bakery.infrastructure.persistence.jpa.CostSettingsJpaRepository;
import com.bakery.infrastructure.persistence.mapper.CostSettingsEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Adaptador de persistencia de la configuración de costeo (una fila por usuario).
 */
@Repository
@Transactional
public class CostSettingsRepositoryImpl implements ICostSettingsRepository {

    private static final BigDecimal DEFAULT_TARGET_MARGIN = new BigDecimal("0.3500");
    private static final BigDecimal DEFAULT_MATERIAL_BASE = new BigDecimal("1.00");
    private static final String DEFAULT_CURRENCY = "ARS";

    private final CostSettingsJpaRepository jpaRepository;
    private final CostSettingsEntityMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public CostSettingsRepositoryImpl(CostSettingsJpaRepository jpaRepository,
                                      CostSettingsEntityMapper mapper,
                                      CurrentUserProvider currentUserProvider) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CostSettings> get() {
        return jpaRepository.findByUserId(currentUserProvider.getCurrentUserId())
                .map(mapper::toDomain);
    }

    @Override
    public CostSettings save(CostSettings settings) {
        Integer userId = currentUserProvider.getCurrentUserId();
        CostSettingsEntity entity = jpaRepository.findByUserId(userId)
                .orElseGet(() -> new CostSettingsEntity(
                        userId,
                        settings.getDefaultTargetMargin(),
                        settings.getMonthlyMaterialBase(),
                        settings.getCurrency()));
        entity.setDefaultTargetMargin(settings.getDefaultTargetMargin());
        entity.setMonthlyMaterialBase(settings.getMonthlyMaterialBase());
        entity.setCurrency(settings.getCurrency());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    public void createDefaultFor(Integer userId) {
        jpaRepository.save(new CostSettingsEntity(
                userId, DEFAULT_TARGET_MARGIN, DEFAULT_MATERIAL_BASE, DEFAULT_CURRENCY));
    }
}
