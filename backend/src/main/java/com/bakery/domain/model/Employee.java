package com.bakery.domain.model;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;
import java.util.Optional;

/**
 * Mano de obra mensual itemizada.
 * La suma de los sueldos es L en COSTING_MODEL.md.
 * Las horas mensuales son opcionales: habilitan la métrica informativa costo/hora.
 */
public class Employee {

    private static final MathContext DIVISION_CONTEXT = new MathContext(10);

    private final Integer id;
    private final String name;
    private BigDecimal monthlySalary;
    private BigDecimal monthlyHours; // nullable

    public Employee(String name, BigDecimal monthlySalary, BigDecimal monthlyHours) {
        this(null, name, monthlySalary, monthlyHours);
    }

    /** Constructor de rehidratación (usado por los mappers de persistencia). */
    public Employee(Integer id, String name, BigDecimal monthlySalary, BigDecimal monthlyHours) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Employee name must not be blank");
        }
        validateSalary(monthlySalary);
        validateHours(monthlyHours);
        this.id = id;
        this.name = name.trim();
        this.monthlySalary = monthlySalary;
        this.monthlyHours = monthlyHours;
    }

    public void updateMonthlySalary(BigDecimal newSalary) {
        validateSalary(newSalary);
        this.monthlySalary = newSalary;
    }

    public void updateMonthlyHours(BigDecimal newHours) {
        validateHours(newHours);
        this.monthlyHours = newHours;
    }

    /**
     * Costo por hora de trabajo (sueldo / horas). Métrica informativa:
     * NO se usa para imputar costos a productos (ver COSTING_MODEL.md, sección 5).
     */
    public Optional<BigDecimal> costPerHour() {
        if (monthlyHours == null) {
            return Optional.empty();
        }
        return Optional.of(monthlySalary.divide(monthlyHours, DIVISION_CONTEXT));
    }

    private static void validateSalary(BigDecimal salary) {
        if (salary == null || salary.signum() < 0) {
            throw new IllegalArgumentException("Employee monthly salary must be >= 0");
        }
    }

    private static void validateHours(BigDecimal hours) {
        if (hours != null && hours.signum() <= 0) {
            throw new IllegalArgumentException("Employee monthly hours must be > 0 when provided");
        }
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public Optional<BigDecimal> getMonthlyHours() {
        return Optional.ofNullable(monthlyHours);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Employee)) return false;
        Employee other = (Employee) o;
        return id != null && id.equals(other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Employee{id=" + id + ", name='" + name + "', monthlySalary=" + monthlySalary
                + ", monthlyHours=" + monthlyHours + "}";
    }
}
