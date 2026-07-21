package com.bakery.application.service;

import com.bakery.application.exception.FixedCostNotFoundException;
import com.bakery.application.port.IFixedCostRepository;
import com.bakery.domain.model.FixedCost;
import com.bakery.domain.model.FixedCostCategory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Casos de uso de costos fijos mensuales (por período).
 */
@Service
public class FixedCostService {

    private final IFixedCostRepository fixedCostRepository;

    public FixedCostService(IFixedCostRepository fixedCostRepository) {
        this.fixedCostRepository = fixedCostRepository;
    }

    public FixedCost createFixedCost(String name, BigDecimal monthlyAmount,
                                      FixedCostCategory category, YearMonth period) {
        return fixedCostRepository.save(new FixedCost(name, monthlyAmount, category, period));
    }

    public FixedCost getFixedCostById(Integer id) {
        return fixedCostRepository.findById(id)
                .orElseThrow(() -> new FixedCostNotFoundException(id));
    }

    public List<FixedCost> getFixedCostsForPeriod(YearMonth period) {
        return fixedCostRepository.findByPeriod(period);
    }

    public FixedCost updateFixedCostAmount(Integer id, BigDecimal newAmount) {
        FixedCost fixedCost = getFixedCostById(id);
        fixedCost.updateMonthlyAmount(newAmount);
        return fixedCostRepository.save(fixedCost);
    }

    public FixedCost updateFixedCostCategory(Integer id, FixedCostCategory newCategory) {
        FixedCost fixedCost = getFixedCostById(id);
        fixedCost.updateCategory(newCategory);
        return fixedCostRepository.save(fixedCost);
    }

    public void deleteFixedCost(Integer id) {
        getFixedCostById(id); // 404 si no existe
        fixedCostRepository.deleteById(id);
    }

    /** F: total de costos fijos del período. */
    public BigDecimal getMonthlyTotal(YearMonth period) {
        return getFixedCostsForPeriod(period).stream()
                .map(FixedCost::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Mes &lt;= period más reciente que tenga al menos un costo fijo cargado. */
    public Optional<YearMonth> findMostRecentPeriodWithData(YearMonth period) {
        return fixedCostRepository.findMostRecentPeriodWithDataUpTo(period);
    }

    /**
     * Copia los costos fijos de fromPeriod hacia toPeriod (mismo nombre, monto y categoría).
     * Si en toPeriod ya existe un costo fijo con el mismo nombre, esa fila se omite (no se pisa
     * ni duplica) — operación aditiva e idempotente, segura ante clicks repetidos.
     */
    public List<FixedCost> duplicateFromPreviousPeriod(YearMonth fromPeriod, YearMonth toPeriod) {
        List<FixedCost> source = fixedCostRepository.findByPeriod(fromPeriod);
        Set<String> existingNames = fixedCostRepository.findByPeriod(toPeriod).stream()
                .map(FixedCost::getName)
                .collect(Collectors.toCollection(HashSet::new));

        return source.stream()
                .filter(fixedCost -> !existingNames.contains(fixedCost.getName()))
                .map(fixedCost -> fixedCostRepository.save(new FixedCost(
                        fixedCost.getName(),
                        fixedCost.getMonthlyAmount(),
                        fixedCost.getCategory(),
                        toPeriod)))
                .collect(Collectors.toList());
    }
}
