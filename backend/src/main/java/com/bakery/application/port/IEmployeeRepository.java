package com.bakery.application.port;

import com.bakery.domain.model.Employee;

import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de empleados (mano de obra mensual, por período).
 */
public interface IEmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(Integer id);

    List<Employee> findByPeriod(YearMonth period);

    /** Mes &lt;= period más reciente que tenga al menos una fila, o vacío si no hay ninguno. */
    Optional<YearMonth> findMostRecentPeriodWithDataUpTo(YearMonth period);

    void deleteById(Integer id);
}
