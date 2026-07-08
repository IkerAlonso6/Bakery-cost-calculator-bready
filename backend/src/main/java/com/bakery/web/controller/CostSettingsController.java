package com.bakery.web.controller;

import com.bakery.application.dto.CostSettingsDTO;
import com.bakery.application.mapper.CostSettingsMapper;
import com.bakery.application.service.CostSettingsService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Configuración global de costeo (margen objetivo, base de materiales M, moneda).
 * Recurso único: GET y PUT sin id.
 */
@RestController
@RequestMapping("/api/cost-settings")
public class CostSettingsController {

    private final CostSettingsService costSettingsService;
    private final CostSettingsMapper costSettingsMapper;

    public CostSettingsController(CostSettingsService costSettingsService,
                                  CostSettingsMapper costSettingsMapper) {
        this.costSettingsService = costSettingsService;
        this.costSettingsMapper = costSettingsMapper;
    }

    @GetMapping
    public CostSettingsDTO get() {
        return costSettingsMapper.toDto(costSettingsService.getSettings());
    }

    @PutMapping
    public CostSettingsDTO update(@Valid @RequestBody CostSettingsDTO dto) {
        var updated = costSettingsService.updateSettings(costSettingsMapper.toDomain(dto));
        return costSettingsMapper.toDto(updated);
    }
}
