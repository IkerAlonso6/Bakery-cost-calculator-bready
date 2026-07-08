package com.bakery.application.service;

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
    private final CostingService costingService;

    public CostingAppService(ProductService productService,
                             FixedCostService fixedCostService,
                             EmployeeService employeeService,
                             CostSettingsService costSettingsService) {
        this.productService = productService;
        this.fixedCostService = fixedCostService;
        this.employeeService = employeeService;
        this.costSettingsService = costSettingsService;
        this.costingService = new CostingService();
    }

    /** Costeo completo de un producto (desglose, precio sugerido, margen real). */
    public ProductCosting getProductCosting(Integer productId) {
        Product product = productService.getProductById(productId);
        return costingService.calculate(
                product,
                fixedCostService.getAllFixedCosts(),
                employeeService.getAllEmployees(),
                costSettingsService.getSettings()
        );
    }

    /** Producto asociado (para armar la respuesta enriquecida en la capa web). */
    public Product getProduct(Integer productId) {
        return productService.getProductById(productId);
    }
}
