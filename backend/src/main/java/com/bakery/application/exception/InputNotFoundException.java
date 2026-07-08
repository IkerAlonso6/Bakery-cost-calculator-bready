package com.bakery.application.exception;

public class InputNotFoundException extends RuntimeException {

    public InputNotFoundException(Integer id) {
        super("Input not found with id: " + id);
    }
}
