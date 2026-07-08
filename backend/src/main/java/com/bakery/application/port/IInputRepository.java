package com.bakery.application.port;

import com.bakery.domain.model.Input;

import java.util.List;
import java.util.Optional;

/**
 * Port de persistencia de insumos. Habla en objetos de dominio;
 * la implementación vive en infrastructure.
 */
public interface IInputRepository {

    Input save(Input input);

    Optional<Input> findById(Integer id);

    List<Input> findAll();

    void deleteById(Integer id);
}
