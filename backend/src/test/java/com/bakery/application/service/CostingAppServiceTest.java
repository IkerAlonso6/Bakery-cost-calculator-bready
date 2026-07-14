package com.bakery.application.service;

import com.bakery.application.dto.ProductCostingDTO;
import com.bakery.application.exception.ProductNotFoundException;
import com.bakery.application.mapper.ProductCostingMapper;
import com.bakery.domain.model.CostSettings;
import com.bakery.domain.model.Employee;
import com.bakery.domain.model.FixedCost;
import com.bakery.domain.model.Ingredient;
import com.bakery.domain.model.Input;
import com.bakery.domain.model.Product;
import com.bakery.domain.model.Recipe;
import com.bakery.domain.model.UnitOfMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

/**
 * CostingAppService construye su propio CostingService internamente (new,
 * sin inyección), así que estos tests ejercen la orquestación real con datos
 * conocidos, reproduciendo el ejemplo numérico de COSTING_MODEL.md (igual
 * que CostingServiceTest de dominio).
 */
@ExtendWith(MockitoExtension.class)
class CostingAppServiceTest {

    @Mock
    private ProductService productService;
    @Mock
    private FixedCostService fixedCostService;
    @Mock
    private EmployeeService employeeService;
    @Mock
    private CostSettingsService costSettingsService;

    private CostingAppService costingAppService;

    private List<FixedCost> fixedCosts;
    private List<Employee> employees;
    private CostSettings settings;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        costingAppService = new CostingAppService(
                productService, fixedCostService, employeeService, costSettingsService, new ProductCostingMapper());

        // F = 200.000
        fixedCosts = List.of(
                new FixedCost(1, "Gas", new BigDecimal("30000")),
                new FixedCost(2, "Agua", new BigDecimal("10000")),
                new FixedCost(3, "Luz", new BigDecimal("40000")),
                new FixedCost(4, "Alquiler", new BigDecimal("120000"))
        );
        // L = 600.000
        employees = List.of(
                new Employee(1, "Panadero", new BigDecimal("400000"), new BigDecimal("160")),
                new Employee(2, "Ayudante", new BigDecimal("200000"), new BigDecimal("160"))
        );
        // M = 800.000, margen global 35%
        settings = new CostSettings(new BigDecimal("0.35"), new BigDecimal("800000"), "ARS");

        Input harina = new Input(1, "Harina 000", UnitOfMeasurement.KILOGRAM, new BigDecimal("1000"));
        Input levadura = new Input(2, "Levadura", UnitOfMeasurement.KILOGRAM, new BigDecimal("10000"));
        Input sal = new Input(3, "Sal", UnitOfMeasurement.KILOGRAM, new BigDecimal("2000"));

        recipe = new Recipe(1, "Bagux Tradicional", new BigDecimal("4"), UnitOfMeasurement.UNIT);
        recipe.addIngredient(new Ingredient(1, harina, new BigDecimal("1")));
        recipe.addIngredient(new Ingredient(2, levadura, new BigDecimal("0.02")));
        recipe.addIngredient(new Ingredient(3, sal, new BigDecimal("0.02")));
    }

    @Test
    @DisplayName("Reproduce el ejemplo de COSTING_MODEL.md: desglose completo y precio sugerido")
    void getProductPricingReproducesTheCostingModelExample() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);
        when(productService.getProductById(1)).thenReturn(product);
        when(costSettingsService.getSettings()).thenReturn(settings);
        when(fixedCostService.getAllFixedCosts()).thenReturn(fixedCosts);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ProductCostingDTO dto = costingAppService.getProductPricing(1);

        assertEquals(new BigDecimal("310.00"), dto.getMaterialCost());
        assertEquals(new BigDecimal("232.50"), dto.getLaborCost());
        assertEquals(new BigDecimal("77.50"), dto.getFixedCost());
        assertEquals(new BigDecimal("620.00"), dto.getTotalCost());
        assertEquals(new BigDecimal("953.85"), dto.getSuggestedPrice());
        assertEquals("ARS", dto.getCurrency());
        assertNull(dto.getPrice());
        assertNull(dto.getRealMargin());
    }

    @Test
    @DisplayName("Incluye el margen real cuando el producto tiene precio manual")
    void getProductPricingIncludesRealMarginWhenProductHasManualPrice() {
        Product product = new Product(1, "Bagux Tradicional", recipe, new BigDecimal("954"), null);
        when(productService.getProductById(1)).thenReturn(product);
        when(costSettingsService.getSettings()).thenReturn(settings);
        when(fixedCostService.getAllFixedCosts()).thenReturn(fixedCosts);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ProductCostingDTO dto = costingAppService.getProductPricing(1);

        assertEquals(new BigDecimal("954"), dto.getPrice());
        assertEquals(new BigDecimal("0.3501"), dto.getRealMargin());
    }

    @Test
    @DisplayName("El override de margen del producto cambia el precio sugerido")
    void getProductPricingWithProductMarginOverride() {
        Product product = new Product(1, "Bagux Premium", recipe, null, new BigDecimal("0.50"));
        when(productService.getProductById(1)).thenReturn(product);
        when(costSettingsService.getSettings()).thenReturn(settings);
        when(fixedCostService.getAllFixedCosts()).thenReturn(fixedCosts);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        ProductCostingDTO dto = costingAppService.getProductPricing(1);

        assertEquals(new BigDecimal("0.50"), dto.getAppliedMargin());
        assertEquals(new BigDecimal("1240.00"), dto.getSuggestedPrice());
    }

    @Test
    @DisplayName("Propaga ProductNotFoundException y nunca consulta settings, fijos ni empleados")
    void getProductPricingThrowsProductNotFoundExceptionAndNeverCallsCostSettings() {
        when(productService.getProductById(99)).thenThrow(new ProductNotFoundException(99));

        assertThrows(ProductNotFoundException.class, () -> costingAppService.getProductPricing(99));

        verifyNoInteractions(costSettingsService, fixedCostService, employeeService);
    }

    @Test
    @DisplayName("Propaga IllegalStateException si la configuración de costeo no está lista")
    void getProductPricingThrowsIllegalStateExceptionWhenSettingsNotConfigured() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);
        when(productService.getProductById(1)).thenReturn(product);
        when(costSettingsService.getSettings())
                .thenThrow(new IllegalStateException("Cost settings are not configured yet."));

        assertThrows(IllegalStateException.class, () -> costingAppService.getProductPricing(1));

        verifyNoInteractions(fixedCostService, employeeService);
    }
}
