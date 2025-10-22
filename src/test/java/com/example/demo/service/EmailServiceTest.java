package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test unitari per EmailService.
 * 
 * Testa l'invio delle email di notifica senza inviare email reali.
 */
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    private Appuntamento appuntamento;

    @BeforeEach
    void setUp() {
        appuntamento = new Appuntamento();
        appuntamento.setId(1L);
        appuntamento.setNomeCliente("Mario");
        appuntamento.setCognomeCliente("Rossi");
        appuntamento.setEmail("mario.rossi@example.com");
        appuntamento.setTelefono("3331234567");
        appuntamento.setDataAppuntamento(LocalDateTime.of(2025, 10, 20, 10, 0));
        appuntamento.setTipoServizio("Ristrutturazione");
    }

    @Test
    void testInviaEmailConfermaAppuntamento() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.inviaEmailConfermaAppuntamento(appuntamento);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void testInviaEmailNotificaAdmin() {
        // Arrange
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        // Act
        emailService.inviaNotificaAdminNuovoAppuntamento(appuntamento);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
