package com.bakery.application.service;

import com.bakery.application.dto.ProductCostingDTO;
import com.bakery.application.mapper.ProductCostingMapper;
import com.bakery.domain.model.CostSettings;
import com.bakery.domain.model.CostingService;
import com.bakery.domain.model.Employee;
import com.bakery.domain.model.FixedCost;
import com.bakery.domain.model.Product;
import com.bakery.domain.model.ProductCosting;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Orquesta el costeo de productos: reúne el producto, los costos fijos y
 * los empleados del período correspondiente, y delega el cálculo en el
 * CostingService de dominio (fórmulas en COSTING_MODEL.md). CostingService
 * en sí no tiene noción de períodos: toda la resolución de "qué mes usar"
 * (incluyendo el fallback a un mes anterior con datos) vive acá.
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
     * Costeo completo de un producto listo para la capa web: desglose de
     * costos, precio sugerido, margen real y moneda, para el período dado
     * (o el mes actual si requestedPeriod es null).
     *
     * Fallback: si el período resuelto no tiene NINGÚN dato propio (ni
     * costos fijos ni empleados), se usa el mes anterior con datos más
     * reciente como "instantánea" completa. Apenas el período tenga aunque
     * sea una sola fila (de cualquiera de las dos listas), se usa tal cual,
     * sin mezclar con otro mes.
     */
    public ProductCostingDTO getProductPricing(Integer productId, YearMonth requestedPeriod) {
        YearMonth period = requestedPeriod != null ? requestedPeriod : YearMonth.now();
        Product product = productService.getProductById(productId);
        CostSettings settings = costSettingsService.getSettings();

        List<FixedCost> fixedCosts = fixedCostService.getFixedCostsForPeriod(period);
        List<Employee> employees = employeeService.getEmployeesForPeriod(period);

        YearMonth resolvedPeriod = period;
        boolean usedFallbackPeriod = false;

        if (fixedCosts.isEmpty() && employees.isEmpty()) {
            Optional<YearMonth> fallback = resolveFallbackPeriod(period);
            if (fallback.isPresent()) {
                resolvedPeriod = fallback.get();
                usedFallbackPeriod = true;
                fixedCosts = fixedCostService.getFixedCostsForPeriod(resolvedPeriod);
                employees = employeeService.getEmployeesForPeriod(resolvedPeriod);
            }
        }

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);
        return productCostingMapper.toDto(product, costing, settings.getCurrency(),
                period, resolvedPeriod, usedFallbackPeriod);
    }

    /**
     * Único mes de respaldo (no uno por lista): el más reciente entre el
     * último mes con costos fijos y el último mes con empleados, para que
     * F y L salgan de una sola instantánea coherente en vez de mezclar meses.
     */
    private Optional<YearMonth> resolveFallbackPeriod(YearMonth upToExclusive) {
        Optional<YearMonth> fixedCostFallback = fixedCostService.findMostRecentPeriodWithData(upToExclusive);
        Optional<YearMonth> employeeFallback = employeeService.findMostRecentPeriodWithData(upToExclusive);

        if (fixedCostFallback.isPresent() && employeeFallback.isPresent()) {
            return Optional.of(fixedCostFallback.get().isAfter(employeeFallback.get())
                    ? fixedCostFallback.get() : employeeFallback.get());
        }
        return fixedCostFallback.isPresent() ? fixedCostFallback : employeeFallback;
    }
}
