package com.bakery.application.port;

import com.bakery.domain.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de productos.
 */
public interface IProductRepository {

    Product save(Product product);

    Optional<Product> findById(Integer id);

    List<Product> findAll();

    void deleteById(Integer id);
}
