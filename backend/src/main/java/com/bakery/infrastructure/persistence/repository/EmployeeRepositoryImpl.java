package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.IEmployeeRepository;
import com.bakery.domain.model.Employee;
import com.bakery.infrastructure.persistence.entity.EmployeeEntity;
import com.bakery.infrastructure.persistence.jpa.EmployeeJpaRepository;
import com.bakery.infrastructure.persistence.mapper.EmployeeEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia de empleados (scopeado por usuario).
 */
@Repository
@Transactional
public class EmployeeRepositoryImpl implements IEmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeEntityMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public EmployeeRepositoryImpl(EmployeeJpaRepository jpaRepository, EmployeeEntityMapper mapper,
                                  CurrentUserProvider currentUserProvider) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Employee save(Employee employee) {
        EmployeeEntity entity = mapper.toEntity(employee);
        entity.setUserId(currentUserProvider.getCurrentUserId());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Integer id) {
        return jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return jpaRepository.findByUserId(currentUserProvider.getCurrentUserId())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .ifPresent(jpaRepository::delete);
    }
}
