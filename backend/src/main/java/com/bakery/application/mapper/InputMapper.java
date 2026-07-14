package com.bakery.application.mapper;

import com.bakery.application.dto.InputDTO;
import com.bakery.domain.model.Input;
import com.bakery.domain.model.UnitOfMeasurement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convierte Input (dominio) <-> InputDTO.
 */
@Component
public class InputMapper {

    public InputDTO toDto(Input input) {
        return new InputDTO(
                input.getId(),
                input.getName(),
                input.getUnitOfMeasure().name(),
                input.getPrice()
        );
    }

    public List<InputDTO> toDtoList(List<Input> inputs) {
        return inputs.stream().map(this::toDto).collect(Collectors.toList());
    }

    /** Crea el objeto de dominio desde el DTO (sin id: lo asigna la persistencia). */
    public Input toDomain(InputDTO dto) {
        return new Input(
                dto.getName(),
                parseUnit(dto.getUnitOfMeasure()),
                dto.getPrice()
        );
    }

    /** Convierte el nombre del enum validando que exista. */
    public static UnitOfMeasurement parseUnit(String unitName) {
        try {
            return UnitOfMeasurement.valueOf(unitName.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown unit of measurement: " + unitName);
        }
    }
}
