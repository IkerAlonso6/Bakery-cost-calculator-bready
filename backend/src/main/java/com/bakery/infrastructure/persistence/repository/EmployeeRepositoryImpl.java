package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.IEmployeeRepository;
import com.bakery.domain.model.Employee;
import com.bakery.infrastructure.persistence.jpa.EmployeeJpaRepository;
import com.bakery.infrastructure.persistence.mapper.EmployeeEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia de empleados.
 */
@Repository
@Transactional
public class EmployeeRepositoryImpl implements IEmployeeRepository {

    private final EmployeeJpaRepository jpaRepository;
    private final EmployeeEntityMapper mapper;

    public EmployeeRepositoryImpl(EmployeeJpaRepository jpaRepository, EmployeeEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Employee save(Employee employee) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(employee)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Employee> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Employee> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}
