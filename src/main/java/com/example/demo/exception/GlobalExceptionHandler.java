package com.example.demo.exception;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Gestore globale delle eccezioni per tutta l'applicazione.
 * 
 * FUNZIONALITÀ:
 * - Intercetta tutte le eccezioni non gestite
 * - Mostra messaggi user-friendly (non stack trace tecnici)
 * - Log errori per debugging
 * - Redirect appropriati con messaggi flash
 * 
 * ECCEZIONI GESTITE:
 * - IllegalArgumentException → Dati invalidi dal form
 * - IllegalStateException → Slot appuntamento non disponibile
 * - MaxUploadSizeExceededException → File troppo grande
 * - RuntimeException → Errori email/database
 * - Exception → Catch-all per errori imprevisti
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Gestisce errori di validazione dati (es: email invalida, campi mancanti).
     * 
     * ESEMPI:
     * - Email non valida nel form contatti
     * - Telefono in formato sbagliato
     * - File upload con estensione non permessa
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, RedirectAttributes redirectAttributes) {
        System.err.println("❌ Errore validazione: " + ex.getMessage());
        redirectAttributes.addFlashAttribute("error", "⚠️ " + ex.getMessage());
        return "redirect:/";
    }
    
    /**
     * Gestisce errore slot appuntamento non disponibile.
     * 
     * Questo errore si verifica quando due clienti provano a prenotare
     * lo stesso orario quasi contemporaneamente e uno dei due arriva secondo.
     */
    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(IllegalStateException ex, RedirectAttributes redirectAttributes) {
        System.err.println("❌ Errore stato: " + ex.getMessage());
        
        if ("FASCIA_ORARIA_NON_DISPONIBILE".equals(ex.getMessage())) {
            redirectAttributes.addFlashAttribute("error", 
                "⚠️ FASCIA ORARIA NON DISPONIBILE - La data e l'ora selezionate sono già occupate. " +
                "Per favore, scegli un'altra data o un altro orario.");
            return "redirect:/prenota";
        }
        
        redirectAttributes.addFlashAttribute("error", "⚠️ " + ex.getMessage());
        return "redirect:/";
    }
    
    /**
     * Gestisce errore file upload troppo grande.
     * 
     * Spring Boot ha un limite di default (1MB) per upload.
     * Se il file è più grande, viene lanciata questa eccezione.
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUploadSize(MaxUploadSizeExceededException ex, RedirectAttributes redirectAttributes) {
        System.err.println("❌ File troppo grande: " + ex.getMessage());
        redirectAttributes.addFlashAttribute("error", 
            "⚠️ FILE TROPPO GRANDE - Il file caricato supera la dimensione massima consentita (5MB). " +
            "Comprimi l'immagine e riprova.");
        return "redirect:/admin/lavori";
    }
    
    /**
     * Gestisce errori runtime generici (es: email server down, database offline).
     * 
     * CASI COMUNI:
     * - Server email SMTP non raggiungibile
     * - Database temporaneamente offline
     * - Errore durante scrittura file su disco
     */
    @ExceptionHandler(RuntimeException.class)
    public String handleRuntimeException(RuntimeException ex, RedirectAttributes redirectAttributes) {
        System.err.println("❌ Errore runtime: " + ex.getMessage());
        ex.printStackTrace();
        
        // Messaggi specifici per errori email
        if (ex.getMessage() != null && ex.getMessage().contains("email")) {
            redirectAttributes.addFlashAttribute("error", 
                "⚠️ ERRORE INVIO EMAIL - Il sistema non è riuscito a inviare l'email. " +
                "L'operazione è stata salvata ma il cliente non ha ricevuto notifica. " +
                "Contattalo manualmente al telefono.");
        } else {
            redirectAttributes.addFlashAttribute("error", 
                "⚠️ ERRORE DI SISTEMA - Si è verificato un errore imprevisto. " +
                "Riprova tra qualche minuto o contatta l'assistenza tecnica.");
        }
        
        return "redirect:/admin/dashboard";
    }
    
    /**
     * Gestisce errore 404 - Pagina non trovata
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NoHandlerFoundException ex, Model model) {
        System.err.println("❌ 404 - Pagina non trovata: " + ex.getRequestURL());
        
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Pagina Non Trovata");
        model.addAttribute("errorMessage", "Questa pagina è ancora in costruzione!");
        model.addAttribute("errorDetails", "La pagina che stai cercando non esiste o è stata spostata.");
        model.addAttribute("errorGif", "https://media.giphy.com/media/3o7btPCcdNniyf0ArS/giphy.gif"); // Cantiere
        
        return "error";
    }
    
    /**
     * Catch-all per tutte le eccezioni non gestite sopra.
     * 
     * Questo è l'ultimo livello di difesa per evitare che l'utente
     * veda stack trace tecnici spaventosi.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        System.err.println("❌ ERRORE CRITICO NON GESTITO: " + ex.getMessage());
        ex.printStackTrace();
        
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Errore Interno");
        model.addAttribute("errorMessage", "Si è verificato un errore imprevisto del sistema.");
        model.addAttribute("errorDetails", "Il nostro team di muratori digitali è al lavoro per riparare il danno!");
        model.addAttribute("errorGif", "https://media.giphy.com/media/l2JdTkHW1KZPdvPQA/giphy.gif"); // Muro che crolla
        
        // TODO: Inviare email automatica all'admin con stack trace completo
        
        return "error"; // Pagina error.html da creare
    }
}
