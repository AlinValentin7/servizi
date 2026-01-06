package com.example.demo.controller;

import com.example.demo.model.Appuntamento;
import com.example.demo.service.AppuntamentoService;
import com.example.demo.service.WhatsAppService;
import com.example.demo.validator.AppuntamentoValidator;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controller per la gestione degli appuntamenti.
 * 
 * AGGIORNATO: Usa constructor injection invece di field injection (@Autowired)
 * per migliore testabilità e immutabilità.
 */
@Controller
public class AppuntamentoController {
    
    private final AppuntamentoService appuntamentoService;
    private final WhatsAppService whatsAppService;
    private final AppuntamentoValidator appuntamentoValidator;
    
    // Constructor injection - best practice Spring moderna
    public AppuntamentoController(AppuntamentoService appuntamentoService,
                                  WhatsAppService whatsAppService,
                                  AppuntamentoValidator appuntamentoValidator) {
        this.appuntamentoService = appuntamentoService;
        this.whatsAppService = whatsAppService;
        this.appuntamentoValidator = appuntamentoValidator;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("appuntamento", new Appuntamento());
        return "index";
    }
    
    @GetMapping("/prenota")
    public String mostraFormPrenotazione(Model model) {
        model.addAttribute("appuntamento", new Appuntamento());
        return "prenota";
    }
    
    @PostMapping("/prenota")
    public String prenotaAppuntamento(@Valid @ModelAttribute("appuntamento") Appuntamento appuntamento, 
                                      BindingResult result, 
                                      Model model) {
        // Validazione base Spring
        if (result.hasErrors()) {
            return "prenota";
        }
        
        try {
            // NUOVO: Validazione completa con tutte le regole di business
            appuntamentoValidator.validaConFestivita(appuntamento);
            
            // Crea l'appuntamento se tutte le validazioni passano
            Appuntamento saved = appuntamentoService.creaAppuntamento(appuntamento);
            
            // Genera link WhatsApp per contatto rapido
            model.addAttribute("whatsappLink", whatsAppService.generaLinkWhatsApp(saved));
            model.addAttribute("success", true);
            return "conferma-appuntamento";
            
        } catch (IllegalArgumentException e) {
            // Errori di validazione (orari, festivi, campi obbligatori)
            model.addAttribute("error", e.getMessage());
            return "prenota";
            
        } catch (IllegalStateException e) {
            // Slot non disponibile (qualcun altro ha prenotato prima)
            if ("FASCIA_ORARIA_NON_DISPONIBILE".equals(e.getMessage())) {
                model.addAttribute("error", "⚠️ FASCIA ORARIA NON DISPONIBILE - La data e l'ora selezionate sono già occupate. Ti preghiamo di scegliere un'altra data o un altro orario.");
            } else {
                model.addAttribute("error", e.getMessage());
            }
            return "prenota";
        }
    }
}