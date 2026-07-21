package com.bakery.application.service;

import com.bakery.application.exception.EmployeeNotFoundException;
import com.bakery.application.port.IEmployeeRepository;
import com.bakery.domain.model.Employee;
import com.bakery.domain.model.EmployeeCategory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Casos de uso de empleados (mano de obra mensual, por período).
 */
@Service
public class EmployeeService {

    private final IEmployeeRepository employeeRepository;

    public EmployeeService(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(String name, BigDecimal monthlySalary, BigDecimal monthlyHours,
                                    EmployeeCategory category, YearMonth period) {
        return employeeRepository.save(new Employee(name, monthlySalary, monthlyHours, category, period));
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public List<Employee> getEmployeesForPeriod(YearMonth period) {
        return employeeRepository.findByPeriod(period);
    }

    public Employee updateEmployeeSalary(Integer id, BigDecimal newSalary) {
        Employee employee = getEmployeeById(id);
        employee.updateMonthlySalary(newSalary);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployeeHours(Integer id, BigDecimal newHours) {
        Employee employee = getEmployeeById(id);
        employee.updateMonthlyHours(newHours);
        return employeeRepository.save(employee);
    }

    public Employee updateEmployeeCategory(Integer id, EmployeeCategory newCategory) {
        Employee employee = getEmployeeById(id);
        employee.updateCategory(newCategory);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Integer id) {
        getEmployeeById(id); // 404 si no existe
        employeeRepository.deleteById(id);
    }

    /** L: total de sueldos del período. */
    public BigDecimal getMonthlyTotal(YearMonth period) {
        return getEmployeesForPeriod(period).stream()
                .map(Employee::getMonthlySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Mes &lt;= period más reciente que tenga al menos un empleado cargado. */
    public Optional<YearMonth> findMostRecentPeriodWithData(YearMonth period) {
        return employeeRepository.findMostRecentPeriodWithDataUpTo(period);
    }

    /**
     * Copia los empleados de fromPeriod hacia toPeriod (mismo nombre, sueldo, horas y categoría).
     * Si en toPeriod ya existe un empleado con el mismo nombre, esa fila se omite (no se pisa ni
     * duplica) — operación aditiva e idempotente, segura ante clicks repetidos.
     */
    public List<Employee> duplicateFromPreviousPeriod(YearMonth fromPeriod, YearMonth toPeriod) {
        List<Employee> source = employeeRepository.findByPeriod(fromPeriod);
        Set<String> existingNames = employeeRepository.findByPeriod(toPeriod).stream()
                .map(Employee::getName)
                .collect(Collectors.toCollection(HashSet::new));

        return source.stream()
                .filter(employee -> !existingNames.contains(employee.getName()))
                .map(employee -> employeeRepository.save(new Employee(
                        employee.getName(),
                        employee.getMonthlySalary(),
                        employee.getMonthlyHours().orElse(null),
                        employee.getCategory(),
                        toPeriod)))
                .collect(Collectors.toList());
    }
}
