package com.bakery.domain.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;

/**
 * Servicio de dominio que implementa el modelo de costeo (COSTING_MODEL.md).
 *
 * Fórmulas:
 *   F = suma de costos fijos del mes
 *   L = suma de sueldos del mes
 *   M = base mensual de materiales (CostSettings.monthlyMaterialBase)
 *
 *   materialCost   = costo de materiales por unidad (receta / rendimiento)
 *   laborCost      = materialCost * (L / M)
 *   fixedCost      = materialCost * (F / M)
 *   totalCost      = materialCost + laborCost + fixedCost
 *   suggestedPrice = totalCost / (1 - margen)   [margen del producto o global]
 *   realMargin     = (precio - totalCost) / precio   [si hay precio manual]
 *
 * Lógica pura: sin Spring, sin persistencia.
 */
public class CostingService {

    private static final MathContext DIVISION_CONTEXT = new MathContext(12);
    private static final int MONEY_SCALE = 2;
    private static final int MARGIN_SCALE = 4;

    /**
     * Calcula el costeo completo de un producto.
     *
     * @param product    producto con su receta
     * @param fixedCosts costos fijos mensuales del negocio
     * @param employees  empleados del negocio
     * @param settings   parámetros globales de costeo
     */
    public ProductCosting calculate(Product product,
                                    List<FixedCost> fixedCosts,
                                    List<Employee> employees,
                                    CostSettings settings) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null");
        }
        if (fixedCosts == null || employees == null) {
            throw new IllegalArgumentException("Fixed costs and employees lists must not be null");
        }
        if (settings == null) {
            throw new IllegalArgumentException("Cost settings must not be null");
        }

        BigDecimal f = fixedCosts.stream()
                .map(FixedCost::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal l = employees.stream()
                .map(Employee::getMonthlySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal m = settings.getMonthlyMaterialBase();
        // CostSettings ya valida M > 0; defensa extra contra estados inválidos rehidratados.
        if (m == null || m.signum() <= 0) {
            throw new IllegalStateException(
                    "Monthly material base (M) must be configured (> 0) before costing products");
        }

        BigDecimal materialCost = product.getMaterialCostPerUnit();

        BigDecimal laborCost = materialCost.multiply(l).divide(m, DIVISION_CONTEXT);
        BigDecimal fixedCost = materialCost.multiply(f).divide(m, DIVISION_CONTEXT);
        BigDecimal totalCost = materialCost.add(laborCost).add(fixedCost);

        BigDecimal appliedMargin = product.getTargetMargin()
                .orElse(settings.getDefaultTargetMargin());
        if (appliedMargin.compareTo(BigDecimal.ONE) >= 0) {
            throw new IllegalStateException("Target margin must be < 1 to suggest a price");
        }
        BigDecimal suggestedPrice = totalCost.divide(
                BigDecimal.ONE.subtract(appliedMargin), DIVISION_CONTEXT);

        BigDecimal realMargin = product.getPrice()
                .filter(price -> price.signum() > 0)
                .map(price -> price.subtract(totalCost)
                        .divide(price, DIVISION_CONTEXT)
                        .setScale(MARGIN_SCALE, RoundingMode.HALF_UP))
                .orElse(null);

        return new ProductCosting(
                money(materialCost),
                money(laborCost),
                money(fixedCost),
                money(totalCost),
                appliedMargin,
                money(suggestedPrice),
                realMargin
        );
    }

    private static BigDecimal money(BigDecimal value) {
        return value.setScale(MONEY_SCALE, RoundingMode.HALF_UP);
    }
}
