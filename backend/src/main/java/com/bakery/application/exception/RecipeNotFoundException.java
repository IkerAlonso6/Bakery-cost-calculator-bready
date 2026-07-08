package com.bakery.application.exception;

public class RecipeNotFoundException extends RuntimeException {

    public RecipeNotFoundException(Integer id) {
        super("Recipe not found with id: " + id);
    }
}
