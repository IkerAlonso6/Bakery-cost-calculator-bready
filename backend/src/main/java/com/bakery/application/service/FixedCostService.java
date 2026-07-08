package com.bakery.application.service;

import com.bakery.application.exception.FixedCostNotFoundException;
import com.bakery.application.port.IFixedCostRepository;
import com.bakery.domain.model.FixedCost;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso de costos fijos mensuales.
 */
@Service
public class FixedCostService {

    private final IFixedCostRepository fixedCostRepository;

    public FixedCostService(IFixedCostRepository fixedCostRepository) {
        this.fixedCostRepository = fixedCostRepository;
    }

    public FixedCost createFixedCost(String name, BigDecimal monthlyAmount) {
        return fixedCostRepository.save(new FixedCost(name, monthlyAmount));
    }

    public FixedCost getFixedCostById(Integer id) {
        return fixedCostRepository.findById(id)
                .orElseThrow(() -> new FixedCostNotFoundException(id));
    }

    public List<FixedCost> getAllFixedCosts() {
        return fixedCostRepository.findAll();
    }

    public FixedCost updateFixedCostAmount(Integer id, BigDecimal newAmount) {
        FixedCost fixedCost = getFixedCostById(id);
        fixedCost.updateMonthlyAmount(newAmount);
        return fixedCostRepository.save(fixedCost);
    }

    public void deleteFixedCost(Integer id) {
        getFixedCostById(id); // 404 si no existe
        fixedCostRepository.deleteById(id);
    }

    /** F: total de costos fijos del mes. */
    public BigDecimal getMonthlyTotal() {
        return getAllFixedCosts().stream()
                .map(FixedCost::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
