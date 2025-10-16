package com.example.demo.controller;

import com.example.demo.model.Appuntamento;
import com.example.demo.service.AppuntamentoService;
import com.example.demo.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@Controller
public class AppuntamentoController {
    
    @Autowired
    private AppuntamentoService appuntamentoService;
    
    @Autowired
    private WhatsAppService whatsAppService;
    
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
        if (result.hasErrors()) {
            return "prenota";
        }
        
        Appuntamento saved = appuntamentoService.creaAppuntamento(appuntamento);
        model.addAttribute("whatsappLink", whatsAppService.generaLinkWhatsApp(saved));
        model.addAttribute("success", true);
        
        return "conferma-appuntamento";
    }
    
    @PostMapping("/admin/appuntamenti/{id}/stato")
    public String aggiornaStato(@PathVariable Long id, 
                                @RequestParam Appuntamento.StatoAppuntamento stato) {
        appuntamentoService.aggiornaStato(id, stato);
        return "redirect:/admin/appuntamenti";
    }
    
    @PostMapping("/admin/appuntamenti/{id}/elimina")
    public String eliminaAppuntamento(@PathVariable Long id) {
        appuntamentoService.eliminaAppuntamento(id);
        return "redirect:/admin/appuntamenti";
    }
}