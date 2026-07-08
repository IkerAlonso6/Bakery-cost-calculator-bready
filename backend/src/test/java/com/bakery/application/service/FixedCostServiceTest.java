package com.bakery.application.service;

import com.bakery.application.exception.FixedCostNotFoundException;
import com.bakery.application.port.IFixedCostRepository;
import com.bakery.domain.model.FixedCost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedCostServiceTest {

    @Mock
    private IFixedCostRepository fixedCostRepository;

    @InjectMocks
    private FixedCostService fixedCostService;

    private FixedCost gas;

    @BeforeEach
    void setUp() {
        gas = new FixedCost(1, "Gas", new BigDecimal("30000"));
    }

    @Test
    @DisplayName("Crea un costo fijo y lo guarda")
    void createFixedCostSavesAndReturns() {
        when(fixedCostRepository.save(any(FixedCost.class))).thenReturn(gas);

        FixedCost created = fixedCostService.createFixedCost("Gas", new BigDecimal("30000"));

        assertEquals(gas, created);
    }

    @Test
    @DisplayName("Obtiene un costo fijo existente por id")
    void getFixedCostByIdReturnsWhenExists() {
        when(fixedCostRepository.findById(1)).thenReturn(Optional.of(gas));

        assertEquals(gas, fixedCostService.getFixedCostById(1));
    }

    @Test
    @DisplayName("Lanza FixedCostNotFoundException si no existe")
    void getFixedCostByIdThrowsFixedCostNotFoundExceptionWhenMissing() {
        when(fixedCostRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(FixedCostNotFoundException.class, () -> fixedCostService.getFixedCostById(99));
    }

    @Test
    @DisplayName("Devuelve todos los costos fijos del repositorio")
    void getAllFixedCostsReturnsRepositoryList() {
        when(fixedCostRepository.findAll()).thenReturn(List.of(gas));

        assertEquals(1, fixedCostService.getAllFixedCosts().size());
    }

    @Test
    @DisplayName("Actualiza el monto de un costo fijo y lo guarda")
    void updateFixedCostAmountUpdatesAndSaves() {
        when(fixedCostRepository.findById(1)).thenReturn(Optional.of(gas));
        when(fixedCostRepository.save(gas)).thenReturn(gas);

        FixedCost updated = fixedCostService.updateFixedCostAmount(1, new BigDecimal("35000"));

        assertEquals(new BigDecimal("35000"), updated.getMonthlyAmount());
    }

    @Test
    @DisplayName("updateFixedCostAmount lanza FixedCostNotFoundException si no existe")
    void updateFixedCostAmountThrowsWhenNotFound() {
        when(fixedCostRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(FixedCostNotFoundException.class,
                () -> fixedCostService.updateFixedCostAmount(99, new BigDecimal("100")));
        verify(fixedCostRepository, never()).save(any());
    }

    @Test
    @DisplayName("Elimina un costo fijo existente")
    void deleteFixedCostDeletesWhenExists() {
        when(fixedCostRepository.findById(1)).thenReturn(Optional.of(gas));

        fixedCostService.deleteFixedCost(1);

        verify(fixedCostRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteFixedCost lanza FixedCostNotFoundException si no existe")
    void deleteFixedCostThrowsWhenNotFound() {
        when(fixedCostRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(FixedCostNotFoundException.class, () -> fixedCostService.deleteFixedCost(99));
        verify(fixedCostRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("getMonthlyTotal suma todos los costos fijos (F = 200.000)")
    void getMonthlyTotalSumsAllAmounts() {
        List<FixedCost> costs = List.of(
                new FixedCost(1, "Gas", new BigDecimal("30000")),
                new FixedCost(2, "Agua", new BigDecimal("10000")),
                new FixedCost(3, "Luz", new BigDecimal("40000")),
                new FixedCost(4, "Alquiler", new BigDecimal("120000"))
        );
        when(fixedCostRepository.findAll()).thenReturn(costs);

        BigDecimal total = fixedCostService.getMonthlyTotal();

        assertEquals(0, new BigDecimal("200000").compareTo(total));
    }

    @Test
    @DisplayName("getMonthlyTotal devuelve cero si no hay costos fijos")
    void getMonthlyTotalReturnsZeroWhenListIsEmpty() {
        when(fixedCostRepository.findAll()).thenReturn(List.of());

        BigDecimal total = fixedCostService.getMonthlyTotal();

        assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }
}
