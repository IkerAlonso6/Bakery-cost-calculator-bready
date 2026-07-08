package com.bakery.application.service;

import com.bakery.application.port.ICostSettingsRepository;
import com.bakery.domain.model.CostSettings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CostSettingsServiceTest {

    @Mock
    private ICostSettingsRepository costSettingsRepository;

    @InjectMocks
    private CostSettingsService costSettingsService;

    @Test
    @DisplayName("Devuelve la configuración cuando ya está inicializada")
    void getSettingsReturnsSettingsWhenConfigured() {
        CostSettings settings = new CostSettings(new BigDecimal("0.35"), new BigDecimal("800000"), "ARS");
        when(costSettingsRepository.get()).thenReturn(Optional.of(settings));

        assertEquals(settings, costSettingsService.getSettings());
    }

    @Test
    @DisplayName("Lanza IllegalStateException si la configuración no fue inicializada")
    void getSettingsThrowsIllegalStateExceptionWhenNotConfigured() {
        when(costSettingsRepository.get()).thenReturn(Optional.empty());

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> costSettingsService.getSettings());
        assertTrue(ex.getMessage().contains("not configured"));
    }

    @Test
    @DisplayName("updateSettings delega directamente en el repositorio")
    void updateSettingsDelegatesDirectlyToSave() {
        CostSettings settings = new CostSettings(new BigDecimal("0.35"), new BigDecimal("800000"), "ARS");
        when(costSettingsRepository.save(settings)).thenReturn(settings);

        CostSettings result = costSettingsService.updateSettings(settings);

        assertSame(settings, result);
        verify(costSettingsRepository).save(settings);
    }
}
