package com.bakery.application.port;

import com.bakery.domain.model.FixedCost;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de costos fijos mensuales (por período).
 */
public interface IFixedCostRepository {

    FixedCost save(FixedCost fixedCost);

    Optional<FixedCost> findById(Integer id);

    List<FixedCost> findByPeriod(YearMonth period);

    /** Mes &lt;= period más reciente que tenga al menos una fila, o vacío si no hay ninguno. */
    Optional<YearMonth> findMostRecentPeriodWithDataUpTo(YearMonth period);

    void deleteById(Integer id);
}
