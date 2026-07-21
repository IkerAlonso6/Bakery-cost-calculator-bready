package com.bakery.web.controller;

import com.bakery.application.dto.ProductCostingDTO;
import com.bakery.application.dto.ProductDTO;
import com.bakery.application.dto.UpdateMarginRequest;
import com.bakery.application.dto.UpdatePriceRequest;
import com.bakery.application.mapper.ProductMapper;
import com.bakery.application.service.CostingAppService;
import com.bakery.application.service.ProductService;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final CostingAppService costingAppService;
    private final ProductMapper productMapper;

    public ProductController(ProductService productService,
                             CostingAppService costingAppService,
                             ProductMapper productMapper) {
        this.productService = productService;
        this.costingAppService = costingAppService;
        this.productMapper = productMapper;
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(@Valid @RequestBody ProductDTO dto) {
        var created = productService.createProduct(
                dto.getName(), dto.getRecipeId(), dto.getPrice(), dto.getTargetMargin());
        return ResponseEntity.status(HttpStatus.CREATED).body(productMapper.toDto(created));
    }

    @GetMapping
    public List<ProductDTO> getAll() {
        return productMapper.toDtoList(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ProductDTO getById(@PathVariable Integer id) {
        return productMapper.toDto(productService.getProductById(id));
    }

    @PutMapping("/{id}/price")
    public ProductDTO updatePrice(@PathVariable Integer id,
                                  @Valid @RequestBody UpdatePriceRequest request) {
        return productMapper.toDto(productService.updateProductPrice(id, request.getPrice()));
    }

    /** Margen objetivo propio del producto; body con targetMargin null vuelve al global. */
    @PutMapping("/{id}/margin")
    public ProductDTO updateMargin(@PathVariable Integer id,
                                   @Valid @RequestBody UpdateMarginRequest request) {
        return productMapper.toDto(productService.updateProductTargetMargin(id, request.getTargetMargin()));
    }

    /**
     * Endpoint central del sistema: costo total desglosado (materiales +
     * mano de obra + fijos), precio sugerido y margen real, para el período
     * dado (por defecto, el mes actual; ver CostingAppService para el
     * fallback cuando el mes elegido no tiene datos propios).
     * Ver docs/COSTING_MODEL.md.
     */
    @GetMapping("/{id}/pricing")
    public ProductCostingDTO getPricing(@PathVariable Integer id,
                                        @RequestParam(required = false) String period) {
        YearMonth parsedPeriod = period != null ? YearMonth.parse(period) : null;
        return costingAppService.getProductPricing(id, parsedPeriod);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
