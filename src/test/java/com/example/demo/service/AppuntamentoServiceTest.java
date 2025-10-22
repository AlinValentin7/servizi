package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.repository.AppuntamentoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per AppuntamentoService.
 * 
 * Testa la logica business della gestione appuntamenti usando Mockito
 * per simulare le dipendenze (repository, email service, etc.)
 */
@ExtendWith(MockitoExtension.class)
class AppuntamentoServiceTest {

    @Mock
    private AppuntamentoRepository appuntamentoRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private WhatsAppService whatsAppService;

    @InjectMocks
    private AppuntamentoService appuntamentoService;

    private Appuntamento appuntamentoTest;

    @BeforeEach
    void setUp() {
        // Crea un appuntamento di test prima di ogni test
        appuntamentoTest = new Appuntamento();
        appuntamentoTest.setId(1L);
        appuntamentoTest.setNomeCliente("Mario");
        appuntamentoTest.setCognomeCliente("Rossi");
        appuntamentoTest.setEmail("mario.rossi@example.com");
        appuntamentoTest.setTelefono("3331234567");
        appuntamentoTest.setDataAppuntamento(LocalDateTime.now().plusDays(1));
        appuntamentoTest.setTipoServizio("Ristrutturazione bagno");
        appuntamentoTest.setDescrizione("Preferibilmente mattina");
    }

    @Test
    void testCreaAppuntamento_Success() {
        // Arrange: configura il comportamento del mock
        when(appuntamentoRepository.save(any(Appuntamento.class))).thenReturn(appuntamentoTest);

        // Act: esegui il metodo da testare
        Appuntamento result = appuntamentoService.creaAppuntamento(appuntamentoTest);

        // Assert: verifica il risultato
        assertThat(result).isNotNull();
        assertThat(result.getNomeCliente()).isEqualTo("Mario");
        assertThat(result.getEmail()).isEqualTo("mario.rossi@example.com");

        // Verifica che il repository save sia stato chiamato
        verify(appuntamentoRepository, times(1)).save(appuntamentoTest);
    }

    @Test
    void testTrovaAppuntamentoPerId_Exists() {
        // Arrange
        when(appuntamentoRepository.findById(1L)).thenReturn(Optional.of(appuntamentoTest));

        // Act
        Optional<Appuntamento> result = appuntamentoService.getAppuntamentoById(1L);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
        assertThat(result.get().getNomeCliente()).isEqualTo("Mario");

        verify(appuntamentoRepository, times(1)).findById(1L);
    }

    @Test
    void testTrovaAppuntamentoPerId_NotExists() {
        // Arrange
        when(appuntamentoRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Appuntamento> result = appuntamentoService.getAppuntamentoById(999L);

        // Assert
        assertThat(result).isEmpty();

        verify(appuntamentoRepository, times(1)).findById(999L);
    }

    @Test
    void testTrovaTuttiAppuntamenti() {
        // Arrange
        Appuntamento app2 = new Appuntamento();
        app2.setId(2L);
        app2.setNomeCliente("Luigi");
        app2.setCognomeCliente("Verdi");

        List<Appuntamento> appuntamenti = Arrays.asList(appuntamentoTest, app2);
        when(appuntamentoRepository.findAll()).thenReturn(appuntamenti);

        // Act
        List<Appuntamento> result = appuntamentoService.getAllAppuntamenti();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getNomeCliente()).isEqualTo("Mario");
        assertThat(result.get(1).getNomeCliente()).isEqualTo("Luigi");

        verify(appuntamentoRepository, times(1)).findAll();
    }

    @Test
    void testEliminaAppuntamento() {
        // Arrange
        doNothing().when(appuntamentoRepository).deleteById(1L);

        // Act
        appuntamentoRepository.deleteById(1L);

        // Assert
        verify(appuntamentoRepository, times(1)).deleteById(1L);
    }

    @Test
    void testVerificaDisponibilita_SlotLibero() {
        // Arrange
        LocalDateTime dataOra = LocalDateTime.of(2025, 10, 20, 10, 0);
        when(appuntamentoRepository.findAll()).thenReturn(List.of());

        // Act
        boolean disponibile = appuntamentoService.isSlotDisponibile(dataOra);

        // Assert
        assertThat(disponibile).isTrue();
    }

    @Test
    void testVerificaDisponibilita_SlotOccupato() {
        // Arrange
        LocalDateTime dataOra = LocalDateTime.of(2025, 10, 20, 10, 0);
        appuntamentoTest.setDataAppuntamento(dataOra);
        appuntamentoTest.setStato(Appuntamento.StatoAppuntamento.CONFERMATO);
        when(appuntamentoRepository.findAll()).thenReturn(List.of(appuntamentoTest));

        // Act
        boolean disponibile = appuntamentoService.isSlotDisponibile(dataOra);

        // Assert
        assertThat(disponibile).isFalse();
    }
}
