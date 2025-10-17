package com.example.demo.controller;

import com.example.demo.model.Candidatura;
import com.example.demo.service.CandidaturaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller pubblico per "Lavora con noi"
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Controller
@RequestMapping("/lavora-con-noi")
@RequiredArgsConstructor
@Slf4j
public class LavoraConNoiController {

    private final CandidaturaService candidaturaService;

    /**
     * Mostra il form di candidatura
     */
    @GetMapping
    public String mostraForm(Model model) {
        model.addAttribute("candidatura", new Candidatura());
        return "lavora-con-noi";
    }

    /**
     * Invia la candidatura
     */
    @PostMapping
    public String inviaCandidatura(
            @Valid @ModelAttribute("candidatura") Candidatura candidatura,
            BindingResult result,
            @RequestParam(value = "cvFile", required = false) MultipartFile cvFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validazione condizionale: se NON ha selezionato "non ha CF", allora il CF è obbligatorio
        if (!Boolean.TRUE.equals(candidatura.getNonHaCodiceFiscale())) {
            if (candidatura.getCodiceFiscale() == null || candidatura.getCodiceFiscale().trim().isEmpty()) {
                result.rejectValue("codiceFiscale", "error.candidatura", 
                    "Il codice fiscale è obbligatorio. Se non lo possiedi, seleziona la casella apposita.");
            }
        } else {
            // Se ha dichiarato di non avere il CF, impostiamo il campo a null
            candidatura.setCodiceFiscale(null);
        }

        // Validazione errori
        if (result.hasErrors()) {
            log.warn("Errori di validazione nella candidatura");
            return "lavora-con-noi";
        }

        // Validazione CV (opzionale ma consigliato)
        if (cvFile != null && !cvFile.isEmpty()) {
            String contentType = cvFile.getContentType();
            long fileSize = cvFile.getSize();
            
            // Max 5MB
            if (fileSize > 5 * 1024 * 1024) {
                model.addAttribute("cvError", "Il CV non può superare i 5MB");
                return "lavora-con-noi";
            }
            
            // Solo PDF, DOC, DOCX
            if (contentType != null && 
                !contentType.equals("application/pdf") && 
                !contentType.equals("application/msword") &&
                !contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                model.addAttribute("cvError", "Il CV deve essere in formato PDF, DOC o DOCX");
                return "lavora-con-noi";
            }
        }

        try {
            // Salva candidatura
            candidaturaService.salvaCandidatura(candidatura, cvFile);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                "Candidatura inviata con successo! Riceverai una email di conferma a breve.");
            
            log.info("Candidatura inviata con successo per: {} {}", 
                candidatura.getNome(), candidatura.getCognome());
            
            return "redirect:/lavora-con-noi/conferma";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            log.error("Errore nella candidatura: {}", e.getMessage());
            return "lavora-con-noi";
            
        } catch (Exception e) {
            model.addAttribute("errorMessage", 
                "Si è verificato un errore durante l'invio della candidatura. Riprova più tardi.");
            log.error("Errore nell'invio candidatura", e);
            return "lavora-con-noi";
        }
    }

    /**
     * Pagina di conferma
     */
    @GetMapping("/conferma")
    public String conferma() {
        return "conferma-candidatura";
    }
}
