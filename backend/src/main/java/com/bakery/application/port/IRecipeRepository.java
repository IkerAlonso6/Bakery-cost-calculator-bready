package com.bakery.application.port;

import com.bakery.domain.model.Recipe;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de recetas (incluye sus ingredientes).
 */
public interface IRecipeRepository {

    Recipe save(Recipe recipe);

    Optional<Recipe> findById(Integer id);

    List<Recipe> findAll();

    void deleteById(Integer id);
}
