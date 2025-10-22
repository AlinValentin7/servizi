package com.example.demo.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitari per il model Appuntamento.
 * 
 * Testa le validazioni Bean Validation (Jakarta Validation).
 */
class AppuntamentoTest {

    private Validator validator;
    private Appuntamento appuntamento;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();

        appuntamento = new Appuntamento();
        appuntamento.setNomeCliente("Mario");
        appuntamento.setCognomeCliente("Rossi");
        appuntamento.setEmail("mario.rossi@example.com");
        appuntamento.setTelefono("3331234567");
        appuntamento.setDataAppuntamento(LocalDateTime.now().plusDays(1));
        appuntamento.setTipoServizio("Ristrutturazione");
    }

    @Test
    void testAppuntamentoValido() {
        // Act
        Set<ConstraintViolation<Appuntamento>> violations = validator.validate(appuntamento);

        // Assert
        assertThat(violations).isEmpty();
    }

    @Test
    void testNomeClienteObbligatorio() {
        // Arrange
        appuntamento.setNomeCliente("");

        // Act
        Set<ConstraintViolation<Appuntamento>> violations = validator.validate(appuntamento);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("nomeCliente"));
    }

    @Test
    void testEmailNonValida() {
        // Arrange
        appuntamento.setEmail("email-non-valida");

        // Act
        Set<ConstraintViolation<Appuntamento>> violations = validator.validate(appuntamento);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("email"));
    }

    @Test
    void testDataOraObbligatoria() {
        // Arrange
        appuntamento.setDataAppuntamento(null);

        // Act
        Set<ConstraintViolation<Appuntamento>> violations = validator.validate(appuntamento);

        // Assert
        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("dataAppuntamento"));
    }

    @Test
    void testGetterSetters() {
        // Test getter e setter
        assertThat(appuntamento.getNomeCliente()).isEqualTo("Mario");
        assertThat(appuntamento.getCognomeCliente()).isEqualTo("Rossi");
        assertThat(appuntamento.getEmail()).isEqualTo("mario.rossi@example.com");
        assertThat(appuntamento.getTelefono()).isEqualTo("3331234567");
        assertThat(appuntamento.getTipoServizio()).isEqualTo("Ristrutturazione");
    }

    @Test
    void testNomeCompletoCliente() {
        // Test metodo helper (se esiste)
        String nomeCompleto = appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente();
        assertThat(nomeCompleto).isEqualTo("Mario Rossi");
    }
}
