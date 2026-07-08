package com.bakery.application.service;

import com.bakery.application.exception.ProductNotFoundException;
import com.bakery.application.port.IProductRepository;
import com.bakery.domain.model.Product;
import com.bakery.domain.model.Recipe;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso de productos.
 */
@Service
public class ProductService {

    private final IProductRepository productRepository;
    private final RecipeService recipeService;

    public ProductService(IProductRepository productRepository, RecipeService recipeService) {
        this.productRepository = productRepository;
        this.recipeService = recipeService;
    }

    /**
     * Crea un producto asociado a una receta existente.
     * price y targetMargin son opcionales (pueden ser null).
     */
    public Product createProduct(String name, Integer recipeId, BigDecimal price, BigDecimal targetMargin) {
        Recipe recipe = recipeService.getRecipeById(recipeId);
        Product product = new Product(null, name, recipe, price, targetMargin);
        return productRepository.save(product);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProductPrice(Integer id, BigDecimal newPrice) {
        Product product = getProductById(id);
        product.updatePrice(newPrice);
        return productRepository.save(product);
    }

    /** Actualiza el margen objetivo propio; null vuelve al margen global. */
    public Product updateProductTargetMargin(Integer id, BigDecimal newTargetMargin) {
        Product product = getProductById(id);
        product.updateTargetMargin(newTargetMargin);
        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        getProductById(id); // 404 si no existe
        productRepository.deleteById(id);
    }
}
