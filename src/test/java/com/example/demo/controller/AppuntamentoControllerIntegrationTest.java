package com.example.demo.controller;

import com.example.demo.model.Appuntamento;
import com.example.demo.service.AppuntamentoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test di integrazione per AppuntamentoController.
 * 
 * Testa l'integrazione completa tra Controller, Service e View usando MockMvc.
 * Simula richieste HTTP reali senza avviare un server.
 * 
 * AGGIORNATO: Usa @MockitoBean invece di @MockBean (deprecato in Spring Boot 3.4+)
 */
@SpringBootTest
@AutoConfigureMockMvc
class AppuntamentoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AppuntamentoService appuntamentoService;

    @Test
    void testMostraPaginaPrenotazione() throws Exception {
        mockMvc.perform(get("/prenota"))
                .andExpect(status().isOk())
                .andExpect(view().name("prenota"))
                .andExpect(model().attributeExists("appuntamento"));
    }

    @Test
    void testCreaAppuntamento_Success() throws Exception {
        // Arrange - Use a valid time within business hours (8:00-20:00)
        LocalDateTime validDateTime = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        
        Appuntamento appuntamento = new Appuntamento();
        appuntamento.setId(1L);
        appuntamento.setNomeCliente("Mario");
        appuntamento.setCognomeCliente("Rossi");
        appuntamento.setEmail("mario.rossi@example.com");
        appuntamento.setTelefono("3331234567");
        appuntamento.setDataAppuntamento(validDateTime);
        appuntamento.setTipoServizio("Ristrutturazione");
        appuntamento.setIndirizzo("Via Roma 123, Roma");

        when(appuntamentoService.creaAppuntamento(any(Appuntamento.class))).thenReturn(appuntamento);

        // Act & Assert
        mockMvc.perform(post("/prenota")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("nomeCliente", "Mario")
                .param("cognomeCliente", "Rossi")
                .param("email", "mario.rossi@example.com")
                .param("telefono", "3331234567")
                .param("dataAppuntamento", validDateTime.toString())
                .param("tipoServizio", "Ristrutturazione")
                .param("indirizzo", "Via Roma 123, Roma"))
                .andExpect(status().isOk())
                .andExpect(view().name("conferma-appuntamento"))
                .andExpect(model().attributeExists("whatsappLink"))
                .andExpect(model().attribute("success", true));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testListaAppuntamenti_AsAdmin() throws Exception {
        // Arrange
        Appuntamento app1 = new Appuntamento();
        app1.setId(1L);
        app1.setNomeCliente("Mario");

        Appuntamento app2 = new Appuntamento();
        app2.setId(2L);
        app2.setNomeCliente("Luigi");

        when(appuntamentoService.getAllAppuntamenti()).thenReturn(Arrays.asList(app1, app2));

        // Act & Assert
        mockMvc.perform(get("/admin/appuntamenti"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/appuntamenti"))
                .andExpect(model().attributeExists("appuntamenti"));
    }

    @Test
    void testListaAppuntamenti_Unauthorized() throws Exception {
        // Act & Assert - senza autenticazione dovrebbe richiedere login
        mockMvc.perform(get("/admin/appuntamenti"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testConfermaAppuntamento() throws Exception {
        // Arrange
        Appuntamento appuntamento = new Appuntamento();
        appuntamento.setId(1L);
        when(appuntamentoService.getAppuntamentoById(1L)).thenReturn(Optional.of(appuntamento));

        // Act & Assert
        mockMvc.perform(post("/admin/appuntamenti/1/conferma")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/appuntamenti"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testEliminaAppuntamento() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/admin/appuntamenti/1/elimina")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/appuntamenti"));
    }
}
