package com.bakery.application.service;

import com.bakery.application.dto.ProductCostingDTO;
import com.bakery.application.mapper.ProductCostingMapper;
import com.bakery.domain.model.CostSettings;
import com.bakery.domain.model.CostingService;
import com.bakery.domain.model.Product;
import com.bakery.domain.model.ProductCosting;
import org.springframework.stereotype.Service;

/**
 * Orquesta el costeo de productos: reúne el producto, los costos fijos,
 * los empleados y la configuración, y delega el cálculo en el
 * CostingService de dominio (fórmulas en COSTING_MODEL.md).
 *
 * Es el caso de uso central del sistema: GET /api/products/{id}/pricing.
 */
@Service
public class CostingAppService {

    private final ProductService productService;
    private final FixedCostService fixedCostService;
    private final EmployeeService employeeService;
    private final CostSettingsService costSettingsService;
    private final ProductCostingMapper productCostingMapper;
    private final CostingService costingService;

    public CostingAppService(ProductService productService,
                             FixedCostService fixedCostService,
                             EmployeeService employeeService,
                             CostSettingsService costSettingsService,
                             ProductCostingMapper productCostingMapper) {
        this.productService = productService;
        this.fixedCostService = fixedCostService;
        this.employeeService = employeeService;
        this.costSettingsService = costSettingsService;
        this.productCostingMapper = productCostingMapper;
        this.costingService = new CostingService();
    }

    /**
     * Costeo completo de un producto listo para la capa web:
     * desglose de costos, precio sugerido, margen real y moneda.
     */
    public ProductCostingDTO getProductPricing(Integer productId) {
        Product product = productService.getProductById(productId);
        CostSettings settings = costSettingsService.getSettings();
        ProductCosting costing = costingService.calculate(
                product,
                fixedCostService.getAllFixedCosts(),
                employeeService.getAllEmployees(),
                settings
        );
        return productCostingMapper.toDto(product, costing, settings.getCurrency());
    }
}
