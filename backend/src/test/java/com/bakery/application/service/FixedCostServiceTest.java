package com.bakery.application.service;

import com.bakery.application.exception.FixedCostNotFoundException;
import com.bakery.application.port.IFixedCostRepository;
import com.bakery.domain.model.FixedCost;
import com.bakery.domain.model.FixedCostCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FixedCostServiceTest {

    private static final YearMonth PERIOD = YearMonth.of(2026, 7);
    private static final YearMonth PREVIOUS_PERIOD = YearMonth.of(2026, 6);

    @Mock
    private IFixedCostRepository fixedCostRepository;

    @InjectMocks
    private FixedCostService fixedCostService;

    private FixedCost gas;

    @BeforeEach
    void setUp() {
        gas = new FixedCost(1, "Gas", new BigDecimal("30000"), FixedCostCategory.SERVICIOS, PERIOD);
    }

    @Test
    @DisplayName("Crea un costo fijo y lo guarda")
    void createFixedCostSavesAndReturns() {
        when(fixedCostRepository.save(any(FixedCost.class))).thenReturn(gas);

        FixedCost created = fixedCostService.createFixedCost(
                "Gas", new BigDecimal("30000"), FixedCostCategory.SERVICIOS, PERIOD);

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
    @DisplayName("Devuelve los costos fijos del período pedido")
    void getFixedCostsForPeriodReturnsRepositoryList() {
        when(fixedCostRepository.findByPeriod(PERIOD)).thenReturn(List.of(gas));

        assertEquals(1, fixedCostService.getFixedCostsForPeriod(PERIOD).size());
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
    @DisplayName("Actualiza la categoría de un costo fijo y la guarda")
    void updateFixedCostCategoryUpdatesAndSaves() {
        when(fixedCostRepository.findById(1)).thenReturn(Optional.of(gas));
        when(fixedCostRepository.save(gas)).thenReturn(gas);

        FixedCost updated = fixedCostService.updateFixedCostCategory(1, FixedCostCategory.MANTENIMIENTO);

        assertEquals(FixedCostCategory.MANTENIMIENTO, updated.getCategory());
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
    @DisplayName("getMonthlyTotal suma todos los costos fijos del período (F = 200.000)")
    void getMonthlyTotalSumsAllAmounts() {
        List<FixedCost> costs = List.of(
                new FixedCost(1, "Gas", new BigDecimal("30000"), FixedCostCategory.SERVICIOS, PERIOD),
                new FixedCost(2, "Agua", new BigDecimal("10000"), FixedCostCategory.SERVICIOS, PERIOD),
                new FixedCost(3, "Luz", new BigDecimal("40000"), FixedCostCategory.SERVICIOS, PERIOD),
                new FixedCost(4, "Alquiler", new BigDecimal("120000"), FixedCostCategory.ALQUILER, PERIOD)
        );
        when(fixedCostRepository.findByPeriod(PERIOD)).thenReturn(costs);

        BigDecimal total = fixedCostService.getMonthlyTotal(PERIOD);

        assertEquals(0, new BigDecimal("200000").compareTo(total));
    }

    @Test
    @DisplayName("getMonthlyTotal devuelve cero si no hay costos fijos en el período")
    void getMonthlyTotalReturnsZeroWhenListIsEmpty() {
        when(fixedCostRepository.findByPeriod(PERIOD)).thenReturn(List.of());

        BigDecimal total = fixedCostService.getMonthlyTotal(PERIOD);

        assertEquals(0, BigDecimal.ZERO.compareTo(total));
    }

    @Test
    @DisplayName("findMostRecentPeriodWithData delega en el repositorio")
    void findMostRecentPeriodWithDataDelegatesToRepository() {
        when(fixedCostRepository.findMostRecentPeriodWithDataUpTo(PERIOD)).thenReturn(Optional.of(PREVIOUS_PERIOD));

        assertEquals(Optional.of(PREVIOUS_PERIOD), fixedCostService.findMostRecentPeriodWithData(PERIOD));
    }

    @Test
    @DisplayName("duplicateFromPreviousPeriod copia los costos fijos del mes anterior al mes destino")
    void duplicateFromPreviousPeriodCopiesRowsToTargetPeriod() {
        when(fixedCostRepository.findByPeriod(PREVIOUS_PERIOD)).thenReturn(List.of(gas));
        when(fixedCostRepository.findByPeriod(PERIOD)).thenReturn(List.of());
        when(fixedCostRepository.save(any(FixedCost.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<FixedCost> created = fixedCostService.duplicateFromPreviousPeriod(PREVIOUS_PERIOD, PERIOD);

        assertEquals(1, created.size());
        assertEquals(PERIOD, created.get(0).getPeriod());
        assertEquals("Gas", created.get(0).getName());
    }

    @Test
    @DisplayName("duplicateFromPreviousPeriod omite nombres que ya existen en el período destino")
    void duplicateFromPreviousPeriodSkipsExistingNamesInTargetPeriod() {
        FixedCost alreadyInTarget = new FixedCost(2, "Gas", new BigDecimal("32000"), FixedCostCategory.SERVICIOS, PERIOD);
        when(fixedCostRepository.findByPeriod(PREVIOUS_PERIOD)).thenReturn(List.of(gas));
        when(fixedCostRepository.findByPeriod(PERIOD)).thenReturn(List.of(alreadyInTarget));

        List<FixedCost> created = fixedCostService.duplicateFromPreviousPeriod(PREVIOUS_PERIOD, PERIOD);

        assertTrue(created.isEmpty());
        verify(fixedCostRepository, never()).save(any());
    }
}
