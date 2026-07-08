package com.bakery.application.service;

import com.bakery.application.exception.ProductNotFoundException;
import com.bakery.application.exception.RecipeNotFoundException;
import com.bakery.application.port.IProductRepository;
import com.bakery.domain.model.Product;
import com.bakery.domain.model.Recipe;
import com.bakery.domain.model.UnitOfMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private IProductRepository productRepository;

    @Mock
    private RecipeService recipeService;

    @InjectMocks
    private ProductService productService;

    private Recipe recipe;
    private Product product;

    @BeforeEach
    void setUp() {
        recipe = new Recipe(1, "Bagux Tradicional", new BigDecimal("4"), UnitOfMeasurement.UNIT);
        product = new Product(1, "Bagux Tradicional", recipe, null, null);
    }

    @Test
    @DisplayName("Crea un producto resolviendo la receta por id")
    void createProductResolvesRecipeAndSaves() {
        when(recipeService.getRecipeById(1)).thenReturn(recipe);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product created = productService.createProduct("Bagux Tradicional", 1, null, null);

        assertEquals(product, created);
        assertEquals(recipe, created.getRecipe());
    }

    @Test
    @DisplayName("createProduct lanza RecipeNotFoundException y nunca guarda si la receta no existe")
    void createProductThrowsRecipeNotFoundExceptionWhenRecipeMissing() {
        when(recipeService.getRecipeById(99)).thenThrow(new RecipeNotFoundException(99));

        assertThrows(RecipeNotFoundException.class,
                () -> productService.createProduct("X", 99, null, null));
        verifyNoInteractions(productRepository);
    }

    @Test
    @DisplayName("createProduct permite price y targetMargin null")
    void createProductAllowsNullPriceAndTargetMargin() {
        when(recipeService.getRecipeById(1)).thenReturn(recipe);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product created = productService.createProduct("Bagux Tradicional", 1, null, null);

        assertTrue(created.getPrice().isEmpty());
        assertTrue(created.getTargetMargin().isEmpty());
    }

    @Test
    @DisplayName("Obtiene un producto existente por id")
    void getProductByIdReturnsProductWhenExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertEquals(product, productService.getProductById(1));
    }

    @Test
    @DisplayName("Lanza ProductNotFoundException si el producto no existe")
    void getProductByIdThrowsProductNotFoundExceptionWhenMissing() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(99));
    }

    @Test
    @DisplayName("Devuelve todos los productos del repositorio")
    void getAllProductsReturnsRepositoryList() {
        when(productRepository.findAll()).thenReturn(List.of(product));

        assertEquals(1, productService.getAllProducts().size());
    }

    @Test
    @DisplayName("Actualiza el precio de un producto y lo guarda")
    void updateProductPriceUpdatesAndSaves() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product updated = productService.updateProductPrice(1, new BigDecimal("954"));

        assertEquals(new BigDecimal("954"), updated.getPrice().orElseThrow());
    }

    @Test
    @DisplayName("updateProductPrice lanza ProductNotFoundException si no existe")
    void updateProductPriceThrowsWhenProductNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class,
                () -> productService.updateProductPrice(99, new BigDecimal("100")));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateProductPrice rechaza precio negativo")
    void updateProductPriceThrowsIllegalArgumentExceptionWhenNegative() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateProductPrice(1, new BigDecimal("-1")));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Actualiza el margen objetivo propio del producto")
    void updateProductTargetMarginSetsOverrideValue() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        Product updated = productService.updateProductTargetMargin(1, new BigDecimal("0.50"));

        assertEquals(new BigDecimal("0.50"), updated.getTargetMargin().orElseThrow());
    }

    @Test
    @DisplayName("updateProductTargetMargin con null vuelve al margen global")
    void updateProductTargetMarginWithNullResetsToGlobalMargin() {
        Product productWithMargin = new Product(1, "Bagux Premium", recipe, null, new BigDecimal("0.50"));
        when(productRepository.findById(1)).thenReturn(Optional.of(productWithMargin));
        when(productRepository.save(productWithMargin)).thenReturn(productWithMargin);

        Product updated = productService.updateProductTargetMargin(1, null);

        assertTrue(updated.getTargetMargin().isEmpty());
    }

    @Test
    @DisplayName("updateProductTargetMargin rechaza valores fuera de [0,1)")
    void updateProductTargetMarginThrowsIllegalArgumentExceptionWhenOutOfRange() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        assertThrows(IllegalArgumentException.class,
                () -> productService.updateProductTargetMargin(1, new BigDecimal("1.0")));
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Elimina un producto existente")
    void deleteProductDeletesWhenExists() {
        when(productRepository.findById(1)).thenReturn(Optional.of(product));

        productService.deleteProduct(1);

        verify(productRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteProduct lanza ProductNotFoundException si no existe")
    void deleteProductThrowsWhenNotFound() {
        when(productRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(99));
        verify(productRepository, never()).deleteById(any());
    }
}
