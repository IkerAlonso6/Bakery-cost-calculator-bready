package com.bakery.web.controller;

import com.bakery.application.dto.DuplicatePreviousPeriodRequest;
import com.bakery.application.dto.FixedCostDTO;
import com.bakery.application.mapper.FixedCostMapper;
import com.bakery.application.service.FixedCostService;
import com.bakery.domain.model.FixedCostCategory;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.YearMonth;
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
        var created = fixedCostService.createFixedCost(
                dto.getName(), dto.getMonthlyAmount(),
                FixedCostCategory.valueOf(dto.getCategory()), YearMonth.parse(dto.getPeriod()));
        return ResponseEntity.status(HttpStatus.CREATED).body(fixedCostMapper.toDto(created));
    }

    /** Lista los costos fijos del período dado (por defecto, el mes actual). */
    @GetMapping
    public List<FixedCostDTO> getAll(@RequestParam(required = false) String period) {
        return fixedCostMapper.toDtoList(fixedCostService.getFixedCostsForPeriod(resolvePeriod(period)));
    }

    /** F: total de costos fijos del período (por defecto, el mes actual). Para el dashboard. */
    @GetMapping("/total")
    public BigDecimal getMonthlyTotal(@RequestParam(required = false) String period) {
        return fixedCostService.getMonthlyTotal(resolvePeriod(period));
    }

    @GetMapping("/{id}")
    public FixedCostDTO getById(@PathVariable Integer id) {
        return fixedCostMapper.toDto(fixedCostService.getFixedCostById(id));
    }

    /** Actualiza monto y categoría. El período de una fila no se edita. */
    @PutMapping("/{id}")
    public FixedCostDTO update(@PathVariable Integer id,
                               @Valid @RequestBody FixedCostDTO dto) {
        fixedCostService.updateFixedCostAmount(id, dto.getMonthlyAmount());
        var updated = fixedCostService.updateFixedCostCategory(id, FixedCostCategory.valueOf(dto.getCategory()));
        return fixedCostMapper.toDto(updated);
    }

    /** Copia los costos fijos de fromPeriod hacia toPeriod (omite nombres ya existentes en toPeriod). */
    @PostMapping("/duplicate-previous-period")
    public List<FixedCostDTO> duplicatePreviousPeriod(@Valid @RequestBody DuplicatePreviousPeriodRequest request) {
        var created = fixedCostService.duplicateFromPreviousPeriod(
                YearMonth.parse(request.getFromPeriod()), YearMonth.parse(request.getToPeriod()));
        return fixedCostMapper.toDtoList(created);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        fixedCostService.deleteFixedCost(id);
        return ResponseEntity.noContent().build();
    }

    private static YearMonth resolvePeriod(String period) {
        return period != null ? YearMonth.parse(period) : YearMonth.now();
    }
}
