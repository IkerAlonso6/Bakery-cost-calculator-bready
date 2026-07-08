package com.bakery.application.port;

import com.bakery.domain.model.FixedCost;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de costos fijos mensuales.
 */
public interface IFixedCostRepository {

    FixedCost save(FixedCost fixedCost);

    Optional<FixedCost> findById(Integer id);

    List<FixedCost> findAll();

    void deleteById(Integer id);
}
