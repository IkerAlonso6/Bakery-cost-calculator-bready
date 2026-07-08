package com.bakery.application.port;

import com.bakery.domain.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de empleados (mano de obra mensual).
 */
public interface IEmployeeRepository {

    Employee save(Employee employee);

    Optional<Employee> findById(Integer id);

    List<Employee> findAll();

    void deleteById(Integer id);
}
