package com.bakery.application.mapper;

import com.bakery.application.dto.FixedCostDTO;
import com.bakery.domain.model.FixedCost;
import com.bakery.domain.model.FixedCostCategory;
import org.springframework.stereotype.Component;

import java.time.YearMonth;
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
                fixedCost.getMonthlyAmount(),
                fixedCost.getCategory().name(),
                fixedCost.getPeriod().toString()
        );
    }

    public List<FixedCostDTO> toDtoList(List<FixedCost> fixedCosts) {
        return fixedCosts.stream().map(this::toDto).collect(Collectors.toList());
    }

    public FixedCost toDomain(FixedCostDTO dto) {
        return new FixedCost(
                dto.getName(),
                dto.getMonthlyAmount(),
                FixedCostCategory.valueOf(dto.getCategory()),
                YearMonth.parse(dto.getPeriod())
        );
    }
}
