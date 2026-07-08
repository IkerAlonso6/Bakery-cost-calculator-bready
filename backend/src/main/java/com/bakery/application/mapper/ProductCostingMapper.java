package com.bakery.application.mapper;

import com.bakery.application.dto.ProductCostingDTO;
import com.bakery.domain.model.Product;
import com.bakery.domain.model.ProductCosting;
import org.springframework.stereotype.Component;

/**
 * Convierte el resultado del costeo (ProductCosting) -> ProductCostingDTO,
 * enriquecido con datos del producto y la moneda del negocio.
 */
@Component
public class ProductCostingMapper {

    public ProductCostingDTO toDto(Product product, ProductCosting costing, String currency) {
        return new ProductCostingDTO(
                product.getId(),
                product.getName(),
                costing.materialCost(),
                costing.laborCost(),
                costing.fixedCost(),
                costing.totalCost(),
                costing.appliedMargin(),
                costing.suggestedPrice(),
                product.getPrice().orElse(null),
                costing.realMargin(),
                currency
        );
    }
}
