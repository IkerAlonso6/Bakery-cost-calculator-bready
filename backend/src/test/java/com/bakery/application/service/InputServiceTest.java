package com.bakery.application.service;

import com.bakery.application.exception.InputNotFoundException;
import com.bakery.application.port.IInputRepository;
import com.bakery.domain.model.Input;
import com.bakery.domain.model.UnitOfMeasurement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InputServiceTest {

    @Mock
    private IInputRepository inputRepository;

    @InjectMocks
    private InputService inputService;

    private Input harina;

    @BeforeEach
    void setUp() {
        harina = new Input(1, "Harina 000", UnitOfMeasurement.KILOGRAM, new BigDecimal("1000"));
    }

    @Test
    @DisplayName("Crea un insumo y lo guarda en el repositorio")
    void createInputSavesAndReturnsInput() {
        when(inputRepository.save(any(Input.class))).thenReturn(harina);

        Input created = inputService.createInput("Harina 000", UnitOfMeasurement.KILOGRAM, new BigDecimal("1000"));

        assertEquals(harina, created);
        verify(inputRepository).save(any(Input.class));
    }

    @Test
    @DisplayName("Obtiene un insumo existente por id")
    void getInputByIdReturnsInputWhenExists() {
        when(inputRepository.findById(1)).thenReturn(Optional.of(harina));

        assertEquals(harina, inputService.getInputById(1));
    }

    @Test
    @DisplayName("Lanza InputNotFoundException si el insumo no existe")
    void getInputByIdThrowsInputNotFoundExceptionWhenMissing() {
        when(inputRepository.findById(99)).thenReturn(Optional.empty());

        InputNotFoundException ex = assertThrows(InputNotFoundException.class,
                () -> inputService.getInputById(99));
        assertTrue(ex.getMessage().contains("99"));
    }

    @Test
    @DisplayName("Devuelve todos los insumos del repositorio")
    void getAllInputsReturnsRepositoryList() {
        when(inputRepository.findAll()).thenReturn(List.of(harina));

        List<Input> all = inputService.getAllInputs();

        assertEquals(1, all.size());
        assertEquals(harina, all.get(0));
    }

    @Test
    @DisplayName("Actualiza el precio de un insumo y lo guarda")
    void updateInputPriceUpdatesAndSaves() {
        when(inputRepository.findById(1)).thenReturn(Optional.of(harina));
        when(inputRepository.save(harina)).thenReturn(harina);

        Input updated = inputService.updateInputPrice(1, new BigDecimal("1200"));

        assertEquals(new BigDecimal("1200"), updated.getPrice());
        verify(inputRepository).save(harina);
    }

    @Test
    @DisplayName("updateInputPrice lanza InputNotFoundException si el insumo no existe")
    void updateInputPriceThrowsWhenInputNotFound() {
        when(inputRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(InputNotFoundException.class,
                () -> inputService.updateInputPrice(99, new BigDecimal("100")));
        verify(inputRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateInputPrice rechaza precio negativo y no guarda")
    void updateInputPriceThrowsIllegalArgumentExceptionWhenPriceNegative() {
        when(inputRepository.findById(1)).thenReturn(Optional.of(harina));

        assertThrows(IllegalArgumentException.class,
                () -> inputService.updateInputPrice(1, new BigDecimal("-1")));
        verify(inputRepository, never()).save(any());
    }

    @Test
    @DisplayName("Elimina un insumo existente")
    void deleteInputDeletesWhenExists() {
        when(inputRepository.findById(1)).thenReturn(Optional.of(harina));

        inputService.deleteInput(1);

        InOrder order = inOrder(inputRepository);
        order.verify(inputRepository).findById(1);
        order.verify(inputRepository).deleteById(1);
    }

    @Test
    @DisplayName("deleteInput lanza InputNotFoundException y nunca borra si no existe")
    void deleteInputThrowsWhenNotFoundAndNeverCallsDeleteById() {
        when(inputRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(InputNotFoundException.class, () -> inputService.deleteInput(99));
        verify(inputRepository, never()).deleteById(any());
    }
}
