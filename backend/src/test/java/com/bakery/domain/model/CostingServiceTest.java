package com.bakery.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitarios puros (sin Spring) del CostingService.
 * El caso principal reproduce el ejemplo numérico de docs/COSTING_MODEL.md, sección 4.
 */
class CostingServiceTest {

    private CostingService costingService;
    private List<FixedCost> fixedCosts;
    private List<Employee> employees;
    private CostSettings settings;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        costingService = new CostingService();

        // Parámetros del mes (COSTING_MODEL.md, sección 4): F = 200.000
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

        // Receta "Bagux": lote de $1.240 en materiales, rinde 4 unidades
        Input harina = new Input(1, "Harina 000", UnitOfMeasurement.KILOGRAM, new BigDecimal("1000"));
        Input levadura = new Input(2, "Levadura", UnitOfMeasurement.KILOGRAM, new BigDecimal("10000"));
        Input sal = new Input(3, "Sal", UnitOfMeasurement.KILOGRAM, new BigDecimal("2000"));

        recipe = new Recipe(1, "Bagux Tradicional", new BigDecimal("4"), UnitOfMeasurement.UNIT);
        recipe.addIngredient(new Ingredient(1, harina, new BigDecimal("1")));
        recipe.addIngredient(new Ingredient(2, levadura, new BigDecimal("0.02")));
        recipe.addIngredient(new Ingredient(3, sal, new BigDecimal("0.02")));
    }

    @Test
    @DisplayName("Reproduce el ejemplo numérico de COSTING_MODEL.md (desglose y costo total)")
    void calculatesCostBreakdownFromCostingModelExample() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);

        assertEquals(new BigDecimal("310.00"), costing.materialCost());
        assertEquals(new BigDecimal("232.50"), costing.laborCost());   // 310 * (600000/800000)
        assertEquals(new BigDecimal("77.50"), costing.fixedCost());    // 310 * (200000/800000)
        assertEquals(new BigDecimal("620.00"), costing.totalCost());   // 310 * (1 + 1.00)
    }

    @Test
    @DisplayName("Precio sugerido con margen global 35% = 953.85")
    void suggestsPriceFromGlobalTargetMargin() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);

        assertEquals(new BigDecimal("0.35"), costing.appliedMargin());
        assertEquals(new BigDecimal("953.85"), costing.suggestedPrice()); // 620 / 0.65
    }

    @Test
    @DisplayName("El margen real reconstruye el margen objetivo al vender al precio sugerido")
    void realMarginMatchesTargetWhenSellingAtSuggestedPrice() {
        Product product = new Product(1, "Bagux Tradicional", recipe, new BigDecimal("954"), null);

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);

        // (954 - 620) / 954 = 0.3501
        assertEquals(new BigDecimal("0.3501"), costing.realMargin());
    }

    @Test
    @DisplayName("Vender mirando solo materiales da margen real negativo (pérdida)")
    void detectsLossWhenPriceIgnoresIndirectCosts() {
        Product product = new Product(1, "Bagux Tradicional", recipe, new BigDecimal("500"), null);

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);

        // (500 - 620) / 500 = -0.24
        assertEquals(new BigDecimal("-0.2400"), costing.realMargin());
        assertTrue(costing.realMargin().signum() < 0);
    }

    @Test
    @DisplayName("El override de margen del producto pisa al margen global")
    void productMarginOverrideTakesPrecedence() {
        Product product = new Product(1, "Bagux Premium", recipe, null, new BigDecimal("0.50"));

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);

        assertEquals(new BigDecimal("0.50"), costing.appliedMargin());
        assertEquals(new BigDecimal("1240.00"), costing.suggestedPrice()); // 620 / 0.50
    }

    @Test
    @DisplayName("Sin precio manual, el margen real es vacío")
    void realMarginIsEmptyWithoutManualPrice() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);

        ProductCosting costing = costingService.calculate(product, fixedCosts, employees, settings);

        assertTrue(costing.getRealMargin().isEmpty());
    }

    @Test
    @DisplayName("Sin costos fijos ni empleados, el costo total es solo materiales")
    void totalCostEqualsMaterialsWhenNoIndirectCosts() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);

        ProductCosting costing = costingService.calculate(product, List.of(), List.of(), settings);

        assertEquals(new BigDecimal("310.00"), costing.totalCost());
    }

    @Test
    @DisplayName("CostSettings rechaza base de materiales M <= 0 (evita división por cero)")
    void costSettingsRejectsNonPositiveMaterialBase() {
        assertThrows(IllegalArgumentException.class,
                () -> new CostSettings(new BigDecimal("0.35"), BigDecimal.ZERO, "ARS"));
    }

    @Test
    @DisplayName("Valida entradas nulas")
    void rejectsNullArguments() {
        Product product = new Product(1, "Bagux Tradicional", recipe, null, null);

        assertThrows(IllegalArgumentException.class,
                () -> costingService.calculate(null, fixedCosts, employees, settings));
        assertThrows(IllegalArgumentException.class,
                () -> costingService.calculate(product, null, employees, settings));
        assertThrows(IllegalArgumentException.class,
                () -> costingService.calculate(product, fixedCosts, null, settings));
        assertThrows(IllegalArgumentException.class,
                () -> costingService.calculate(product, fixedCosts, employees, null));
    }
}
