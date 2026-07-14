package com.bakery.application.mapper;

import com.bakery.application.dto.FixedCostDTO;
import com.bakery.domain.model.FixedCost;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Convierte FixedCost (dominio) <-> FixedCostDTO.
 */
@Component
public class FixedCostMapper {

    public FixedCostDTO toDto(FixedCost fixedCost) {
        return new FixedCostDTO(
                fixedCost.getId(),
                fixedCost.getName(),
                fixedCost.getMonthlyAmount()
        );
    }

    public List<FixedCostDTO> toDtoList(List<FixedCost> fixedCosts) {
        return fixedCosts.stream().map(this::toDto).collect(Collectors.toList());
    }

    public FixedCost toDomain(FixedCostDTO dto) {
        return new FixedCost(dto.getName(), dto.getMonthlyAmount());
    }
}
