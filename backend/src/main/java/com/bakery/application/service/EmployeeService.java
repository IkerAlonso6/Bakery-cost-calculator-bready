package com.bakery.application.service;

import com.bakery.application.exception.EmployeeNotFoundException;
import com.bakery.application.port.IEmployeeRepository;
import com.bakery.domain.model.Employee;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso de empleados (mano de obra mensual).
 */
@Service
public class EmployeeService {

    private final IEmployeeRepository employeeRepository;

    public EmployeeService(IEmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public Employee createEmployee(String name, BigDecimal monthlySalary, BigDecimal monthlyHours) {
        return employeeRepository.save(new Employee(name, monthlySalary, monthlyHours));
    }

    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
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

    public void deleteEmployee(Integer id) {
        getEmployeeById(id); // 404 si no existe
        employeeRepository.deleteById(id);
    }

    /** L: total de sueldos del mes. */
    public BigDecimal getMonthlyTotal() {
        return getAllEmployees().stream()
                .map(Employee::getMonthlySalary)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
