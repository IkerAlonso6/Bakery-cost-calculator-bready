package com.bakery.application.mapper;

import com.bakery.application.dto.ProductDTO;
import com.bakery.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Convierte Product (dominio) -> ProductDTO.
 * La conversión DTO -> dominio la hace ProductService (necesita resolver
 * la Recipe por id contra el repositorio).
 */
@Component
public class ProductMapper {

    public ProductDTO toDto(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getRecipe().getId(),
                product.getRecipe().getName(),
                product.getPrice().orElse(null),
                product.getTargetMargin().orElse(null)
        );
    }

    public List<ProductDTO> toDtoList(List<Product> products) {
        return products.stream().map(this::toDto).toList();
    }
}
