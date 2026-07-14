package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.CurrentUserProvider;
import com.bakery.application.port.IProductRepository;
import com.bakery.domain.model.Product;
import com.bakery.infrastructure.persistence.entity.ProductEntity;
import com.bakery.infrastructure.persistence.jpa.ProductJpaRepository;
import com.bakery.infrastructure.persistence.mapper.ProductEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia de productos (scopeado por usuario).
 */
@Repository
@Transactional
public class ProductRepositoryImpl implements IProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;
    private final CurrentUserProvider currentUserProvider;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository, ProductEntityMapper mapper,
                                 CurrentUserProvider currentUserProvider) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(product);
        entity.setUserId(currentUserProvider.getCurrentUserId());
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Integer id) {
        return jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return jpaRepository.findByUserId(currentUserProvider.getCurrentUserId())
                .stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.findByIdAndUserId(id, currentUserProvider.getCurrentUserId())
                .ifPresent(jpaRepository::delete);
    }
}
