package com.example.demo.controller;

import com.example.demo.model.Lavoro;
import com.example.demo.model.Appuntamento;
import com.example.demo.model.Contatto;
import com.example.demo.service.LavoroService;
import com.example.demo.service.AppuntamentoService;
import com.example.demo.service.ContattoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private LavoroService lavoroService;
    
    @Autowired
    private AppuntamentoService appuntamentoService;
    
    @Autowired
    private ContattoService contattoService;
    
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    @GetMapping("/login")
    public String loginPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Username o password non validi");
        }
        return "admin/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("appuntamenti", appuntamentoService.getAllAppuntamenti());
        model.addAttribute("contatti", contattoService.getContattiNonLetti());
        model.addAttribute("lavori", lavoroService.getAllLavori());
        return "admin/dashboard";
    }

    // CRUD Lavori
    @GetMapping("/lavori")
    public String gestioneLavori(Model model) {
        model.addAttribute("lavori", lavoroService.getAllLavori());
        return "admin/gestione-lavori";
    }

    @GetMapping("/lavori/nuovo")
    public String nuovoLavoro(Model model) {
        model.addAttribute("lavoro", new Lavoro());
        return "admin/form-lavoro-new";
    }

    @PostMapping("/lavori/salva")
    public String salvaLavoro(@ModelAttribute Lavoro lavoro,
                             @RequestParam(value = "fotoPrimaFiles", required = false) MultipartFile[] fotoPrimaFiles,
                             @RequestParam(value = "fotoDopoFiles", required = false) MultipartFile[] fotoDopoFiles,
                             RedirectAttributes redirectAttributes) {
        
        try {
            // Salva foto prima
            if (fotoPrimaFiles != null) {
                List<String> fotoPrimaUrls = new ArrayList<>();
                for (MultipartFile file : fotoPrimaFiles) {
                    if (!file.isEmpty()) {
                        String filename = saveFile(file);
                        fotoPrimaUrls.add("/uploads/" + filename);
                    }
                }
                lavoro.setFotoPrima(fotoPrimaUrls);
            }

            // Salva foto dopo
            if (fotoDopoFiles != null) {
                List<String> fotoDopoUrls = new ArrayList<>();
                for (MultipartFile file : fotoDopoFiles) {
                    if (!file.isEmpty()) {
                        String filename = saveFile(file);
                        fotoDopoUrls.add("/uploads/" + filename);
                    }
                }
                lavoro.setFotoDopo(fotoDopoUrls);
            }

            lavoroService.salvaLavoro(lavoro);
            redirectAttributes.addFlashAttribute("success", "Lavoro salvato con successo!");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nel salvare il lavoro: " + e.getMessage());
        }
        
        return "redirect:/admin/lavori";
    }

    @GetMapping("/lavori/modifica/{id}")
    public String modificaLavoro(@PathVariable Long id, Model model) {
        Lavoro lavoro = lavoroService.getLavoroById(id).orElse(new Lavoro());
        model.addAttribute("lavoro", lavoro);
        return "admin/form-lavoro-new";
    }

    @PostMapping("/lavori/elimina/{id}")
    public String eliminaLavoro(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            lavoroService.eliminaLavoro(id);
            redirectAttributes.addFlashAttribute("success", "Lavoro eliminato con successo!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'eliminare il lavoro");
        }
        return "redirect:/admin/lavori";
    }

    @PostMapping("/lavori/toggle-pubblicazione/{id}")
    public String togglePubblicazione(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Lavoro lavoro = lavoroService.getLavoroById(id).orElseThrow();
            lavoro.setPubblicato(!lavoro.isPubblicato());
            lavoroService.salvaLavoro(lavoro);
            redirectAttributes.addFlashAttribute("success", "Stato pubblicazione modificato!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nel modificare lo stato");
        }
        return "redirect:/admin/lavori";
    }

    // Gestione Appuntamenti
    @GetMapping("/appuntamenti")
    public String gestioneAppuntamenti(Model model) {
        model.addAttribute("appuntamenti", appuntamentoService.getAllAppuntamenti());
        return "admin/appuntamenti";
    }

    // Gestione Contatti
    @GetMapping("/contatti")
    public String gestioneContatti(Model model) {
        model.addAttribute("contatti", contattoService.getAllContatti());
        return "admin/contatti";
    }

    // Metodo helper per salvare file
    private String saveFile(MultipartFile file) throws IOException {
        // Crea la directory se non esiste
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // Genera nome file univoco
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;

        // Salva il file
        Path filepath = Paths.get(UPLOAD_DIR + filename);
        Files.write(filepath, file.getBytes());

        return filename;
    }
}
