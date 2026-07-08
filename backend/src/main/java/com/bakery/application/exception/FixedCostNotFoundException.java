package com.bakery.application.exception;

public class FixedCostNotFoundException extends RuntimeException {

    public FixedCostNotFoundException(Integer id) {
        super("Fixed cost not found with id: " + id);
    }
}
