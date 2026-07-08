package com.bakery.infrastructure.persistence.repository;

import com.bakery.application.port.IProductRepository;
import com.bakery.domain.model.Product;
import com.bakery.infrastructure.persistence.jpa.ProductJpaRepository;
import com.bakery.infrastructure.persistence.mapper.ProductEntityMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Adaptador de persistencia de productos.
 */
@Repository
@Transactional
public class ProductRepositoryImpl implements IProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final ProductEntityMapper mapper;

    public ProductRepositoryImpl(ProductJpaRepository jpaRepository, ProductEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Product save(Product product) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(product)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Product> findById(Integer id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public void deleteById(Integer id) {
        jpaRepository.deleteById(id);
    }
}
