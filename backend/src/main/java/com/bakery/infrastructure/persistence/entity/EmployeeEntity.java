package com.bakery.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "employees")
public class EmployeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "monthly_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal monthlySalary;

    @Column(name = "monthly_hours", precision = 7, scale = 2)
    private BigDecimal monthlyHours; // nullable

    protected EmployeeEntity() {
        // requerido por JPA
    }

    public EmployeeEntity(Integer id, String name, BigDecimal monthlySalary, BigDecimal monthlyHours) {
        this.id = id;
        this.name = name;
        this.monthlySalary = monthlySalary;
        this.monthlyHours = monthlyHours;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getMonthlySalary() {
        return monthlySalary;
    }

    public void setMonthlySalary(BigDecimal monthlySalary) {
        this.monthlySalary = monthlySalary;
    }

    public BigDecimal getMonthlyHours() {
        return monthlyHours;
    }

    public void setMonthlyHours(BigDecimal monthlyHours) {
        this.monthlyHours = monthlyHours;
    }
}
