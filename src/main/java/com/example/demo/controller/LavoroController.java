package com.example.demo.controller;

import com.example.demo.model.Lavoro;
import com.example.demo.service.LavoroService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller per la gestione dei lavori.
 * AGGIORNATO: Constructor injection.
 */
@Controller
public class LavoroController {
    
    private final LavoroService lavoroService;
    
    public LavoroController(LavoroService lavoroService) {
        this.lavoroService = lavoroService;
    }
    
    @GetMapping("/lavori")
    public String mostraLavori(Model model) {
        model.addAttribute("lavori", lavoroService.getLavoriPubblicati());
        return "lavori";
    }
    
    @GetMapping("/lavori/{id}")
    public String dettaglioLavoro(@PathVariable Long id, Model model) {
        model.addAttribute("lavoro", lavoroService.getLavoroById(id).orElse(null));
        return "dettaglio-lavoro";
    }
}