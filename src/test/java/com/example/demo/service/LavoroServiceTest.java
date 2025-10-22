package com.example.demo.service;

import com.example.demo.model.Lavoro;
import com.example.demo.repository.LavoroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per LavoroService.
 * 
 * Testa la gestione delle offerte di lavoro (creazione, modifica, ricerca).
 */
@ExtendWith(MockitoExtension.class)
class LavoroServiceTest {

    @Mock
    private LavoroRepository lavoroRepository;

    @InjectMocks
    private LavoroService lavoroService;

    private Lavoro lavoroTest;

    @BeforeEach
    void setUp() {
        lavoroTest = new Lavoro();
        lavoroTest.setId(1L);
        lavoroTest.setTitolo("Muratore Esperto");
        lavoroTest.setDescrizione("Cerchiamo muratore con esperienza");
        lavoroTest.setLuogo("Roma");
        lavoroTest.setCategoria("Ristrutturazione");
        lavoroTest.setDataInizio(LocalDate.now().minusMonths(1));
        lavoroTest.setDataFine(LocalDate.now());
        lavoroTest.setPubblicato(true);
    }

    @Test
    void testCreaLavoro_Success() {
        // Arrange
        when(lavoroRepository.save(any(Lavoro.class))).thenReturn(lavoroTest);

        // Act
        Lavoro result = lavoroService.salvaLavoro(lavoroTest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getTitolo()).isEqualTo("Muratore Esperto");
        assertThat(result.isPubblicato()).isTrue();

        verify(lavoroRepository, times(1)).save(lavoroTest);
    }

    @Test
    void testTrovaLavoroPerId_Exists() {
        // Arrange
        when(lavoroRepository.findById(1L)).thenReturn(Optional.of(lavoroTest));

        // Act
        Optional<Lavoro> result = lavoroRepository.findById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getTitolo()).isEqualTo("Muratore Esperto");

        verify(lavoroRepository, times(1)).findById(1L);
    }

    @Test
    void testTrovaLavoriPubblicati() {
        // Arrange
        Lavoro lavoro2 = new Lavoro();
        lavoro2.setId(2L);
        lavoro2.setTitolo("Elettricista");
        lavoro2.setPubblicato(true);

        when(lavoroRepository.findByPubblicatoOrderByDataFineDesc(true)).thenReturn(Arrays.asList(lavoroTest, lavoro2));

        // Act
        List<Lavoro> result = lavoroService.getLavoriPubblicati();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).allMatch(Lavoro::isPubblicato);

        verify(lavoroRepository, times(1)).findByPubblicatoOrderByDataFineDesc(true);
    }

    @Test
    void testGetAllLavori() {
        // Arrange
        when(lavoroRepository.findAll()).thenReturn(Arrays.asList(lavoroTest));

        // Act
        List<Lavoro> result = lavoroService.getAllLavori();

        // Assert
        assertThat(result).hasSize(1);
        verify(lavoroRepository, times(1)).findAll();
    }

    @Test
    void testEliminaLavoro() {
        // Arrange
        doNothing().when(lavoroRepository).deleteById(1L);

        // Act
        lavoroRepository.deleteById(1L);

        // Assert
        verify(lavoroRepository, times(1)).deleteById(1L);
    }
}
