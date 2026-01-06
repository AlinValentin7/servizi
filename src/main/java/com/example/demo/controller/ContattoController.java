package com.example.demo.controller;

import com.example.demo.model.Contatto;
import com.example.demo.service.ContattoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controller per la gestione dei contatti.
 * AGGIORNATO: Constructor injection per migliore design.
 */
@Controller
public class ContattoController {
    
    private final ContattoService contattoService;
    
    public ContattoController(ContattoService contattoService) {
        this.contattoService = contattoService;
    }
    
    @GetMapping("/contatti")
    public String mostraFormContatti(Model model) {
        model.addAttribute("contatto", new Contatto());
        return "contatti";
    }
    
    @PostMapping("/contatti")
    public String inviaContatto(@Valid @ModelAttribute("contatto") Contatto contatto, 
                                BindingResult result, 
                                Model model) {
        if (result.hasErrors()) {
            return "contatti";
        }
        
        contattoService.salvaContatto(contatto);
        model.addAttribute("success", true);
        model.addAttribute("contatto", new Contatto());
        
        return "contatti";
    }
    
    @GetMapping("/admin/contatti/{id}")
    public String visualizzaContatto(@PathVariable Long id, Model model) {
        contattoService.segnaComeLetto(id);
        model.addAttribute("contatto", contattoService.getContattoById(id).orElse(null));
        return "admin/dettaglio-contatto";
    }
    
    @PostMapping("/admin/contatti/{id}/rispondi")
    public String rispondiContatto(@PathVariable Long id, @RequestParam String risposta) {
        contattoService.rispondiContatto(id, risposta);
        return "redirect:/admin/contatti";
    }
    
    @PostMapping("/admin/contatti/{id}/elimina")
    public String eliminaContatto(@PathVariable Long id) {
        contattoService.eliminaContatto(id);
        return "redirect:/admin/contatti";
    }
}