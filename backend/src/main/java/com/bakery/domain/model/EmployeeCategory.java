package com.bakery.domain.model;

/**
 * Categorías fijas para clasificar el rol de un empleado (organizativo,
 * no afecta el cálculo de costeo — ver CostingService).
 */
public enum EmployeeCategory {
    PRODUCCION,
    ADMINISTRACION,
    VENTAS,
    OTROS
}
