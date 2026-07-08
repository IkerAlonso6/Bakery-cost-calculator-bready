package com.bakery.infrastructure.persistence.mapper;

import com.bakery.domain.model.Product;
import com.bakery.infrastructure.persistence.entity.ProductEntity;
import org.springframework.stereotype.Component;

/**
 * Convierte Product (dominio) <-> ProductEntity.
 */
@Component
public class ProductEntityMapper {

    private final RecipeEntityMapper recipeEntityMapper;

    public ProductEntityMapper(RecipeEntityMapper recipeEntityMapper) {
        this.recipeEntityMapper = recipeEntityMapper;
    }

    public ProductEntity toEntity(Product product) {
        return new ProductEntity(
                product.getId(),
                product.getName(),
                product.getPrice().orElse(null),
                product.getTargetMargin().orElse(null),
                recipeEntityMapper.toEntity(product.getRecipe())
        );
    }

    public Product toDomain(ProductEntity entity) {
        return new Product(
                entity.getId(),
                entity.getName(),
                recipeEntityMapper.toDomain(entity.getRecipe()),
                entity.getPrice(),
                entity.getTargetMargin()
        );
    }
}
