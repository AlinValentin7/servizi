package com.example.demo.controller;

import com.example.demo.model.Lavoro;
import com.example.demo.service.LavoroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LavoroController {
    
    @Autowired
    private LavoroService lavoroService;
    
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