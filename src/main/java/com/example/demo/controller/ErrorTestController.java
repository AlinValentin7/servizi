package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * Controller di TEST per simulare e verificare le pagine di errore personalizzate.
 * 
 * ATTENZIONE: Questo controller serve SOLO per testing/sviluppo.
 * Rimuovere o commentare in produzione per motivi di sicurezza!
 * 
 * COME TESTARE LE PAGINE DI ERRORE:
 * 
 * 1. Avvia l'applicazione: mvn spring-boot:run
 * 
 * 2. Apri il browser e vai su:
 *    - http://localhost:8080/test-errors         → Menu di test
 *    - http://localhost:8080/test-errors/404     → Errore 404 Not Found
 *    - http://localhost:8080/test-errors/500     → Errore 500 Internal Server Error
 *    - http://localhost:8080/test-errors/403     → Errore 403 Forbidden
 *    - http://localhost:8080/test-errors/400     → Errore 400 Bad Request
 *    - http://localhost:8080/test-errors/503     → Errore 503 Service Unavailable
 * 
 * 3. Per testare il vero 404, vai su una pagina che non esiste:
 *    - http://localhost:8080/pagina-inesistente
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Controller
@RequestMapping("/test-errors")
public class ErrorTestController {

    /**
     * Pagina menu per testare tutti gli errori
     */
    @GetMapping
    public String errorMenu() {
        return "test-errors-menu";
    }

    /**
     * Simula errore 404 - Pagina Non Trovata
     * Url: http://localhost:8080/test-errors/404
     */
    @GetMapping("/404")
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String test404() throws NoHandlerFoundException {
        throw new NoHandlerFoundException("GET", "/test-404", null);
    }

    /**
     * Simula errore 500 - Errore Interno del Server
     * Url: http://localhost:8080/test-errors/500
     */
    @GetMapping("/500")
    public String test500() {
        // Lancia una RuntimeException per simulare un errore interno
        throw new RuntimeException("Errore di test 500 - Il server ha avuto un problema interno");
    }

    /**
     * Simula errore 403 - Accesso Negato
     * Url: http://localhost:8080/test-errors/403
     * 
     * NOTA: Per testare veramente il 403, prova ad accedere a:
     * http://localhost:8080/admin/dashboard (senza essere loggato)
     */
    @GetMapping("/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String test403() {
        throw new RuntimeException("Accesso negato - Area riservata");
    }

    /**
     * Simula errore 400 - Richiesta Non Valida
     * Url: http://localhost:8080/test-errors/400
     */
    @GetMapping("/400")
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String test400() {
        throw new IllegalArgumentException("Parametri della richiesta non validi");
    }

    /**
     * Simula errore 503 - Servizio Non Disponibile
     * Url: http://localhost:8080/test-errors/503
     */
    @GetMapping("/503")
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public String test503() {
        throw new RuntimeException("Servizio temporaneamente non disponibile");
    }

    /**
     * Simula un NullPointerException (genera 500)
     * Url: http://localhost:8080/test-errors/null
     */
    @GetMapping("/null")
    public String testNullPointer() {
        String str = null;
        str.length(); // Questo genera NullPointerException
        return "redirect:/";
    }

    /**
     * Simula una divisione per zero (genera 500)
     * Url: http://localhost:8080/test-errors/divide
     */
    @GetMapping("/divide")
    public String testDivideByZero() {
        int result = 10 / 0; // Genera ArithmeticException
        return "redirect:/";
    }
}
