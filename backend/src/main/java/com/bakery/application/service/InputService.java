package com.bakery.application.service;

import com.bakery.application.exception.InputNotFoundException;
import com.bakery.application.port.IInputRepository;
import com.bakery.domain.model.Input;
import com.bakery.domain.model.UnitOfMeasurement;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Casos de uso de insumos (materia prima).
 */
@Service
public class InputService {

    private final IInputRepository inputRepository;

    public InputService(IInputRepository inputRepository) {
        this.inputRepository = inputRepository;
    }

    public Input createInput(String name, UnitOfMeasurement unit, BigDecimal price) {
        return inputRepository.save(new Input(name, unit, price));
    }

    public Input getInputById(Integer id) {
        return inputRepository.findById(id)
                .orElseThrow(() -> new InputNotFoundException(id));
    }

    public List<Input> getAllInputs() {
        return inputRepository.findAll();
    }

    public Input updateInputPrice(Integer id, BigDecimal newPrice) {
        Input input = getInputById(id);
        input.updatePrice(newPrice);
        return inputRepository.save(input);
    }

    public void deleteInput(Integer id) {
        getInputById(id); // 404 si no existe
        inputRepository.deleteById(id);
    }
}
