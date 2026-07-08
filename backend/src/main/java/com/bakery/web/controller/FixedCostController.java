package com.bakery.web.controller;

import com.bakery.application.dto.FixedCostDTO;
import com.bakery.application.mapper.FixedCostMapper;
import com.bakery.application.service.FixedCostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/fixed-costs")
public class FixedCostController {

    private final FixedCostService fixedCostService;
    private final FixedCostMapper fixedCostMapper;

    public FixedCostController(FixedCostService fixedCostService, FixedCostMapper fixedCostMapper) {
        this.fixedCostService = fixedCostService;
        this.fixedCostMapper = fixedCostMapper;
    }

    @PostMapping
    public ResponseEntity<FixedCostDTO> create(@Valid @RequestBody FixedCostDTO dto) {
        var created = fixedCostService.createFixedCost(dto.name(), dto.monthlyAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(fixedCostMapper.toDto(created));
    }

    @GetMapping
    public List<FixedCostDTO> getAll() {
        return fixedCostMapper.toDtoList(fixedCostService.getAllFixedCosts());
    }

    /** F: total de costos fijos del mes (para el dashboard). */
    @GetMapping("/total")
    public BigDecimal getMonthlyTotal() {
        return fixedCostService.getMonthlyTotal();
    }

    @GetMapping("/{id}")
    public FixedCostDTO getById(@PathVariable Integer id) {
        return fixedCostMapper.toDto(fixedCostService.getFixedCostById(id));
    }

    @PutMapping("/{id}")
    public FixedCostDTO update(@PathVariable Integer id,
                               @Valid @RequestBody FixedCostDTO dto) {
        return fixedCostMapper.toDto(fixedCostService.updateFixedCostAmount(id, dto.monthlyAmount()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fixedCostService.deleteFixedCost(id);
        return ResponseEntity.noContent().build();
    }
}
