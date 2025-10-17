package com.example.demo.controller;

import com.example.demo.model.Lavoro;
import com.example.demo.model.Appuntamento;
import com.example.demo.model.Contatto;
import com.example.demo.model.Candidatura;
import com.example.demo.service.LavoroService;
import com.example.demo.service.AppuntamentoService;
import com.example.demo.service.ContattoService;
import com.example.demo.service.CandidaturaService;
import com.example.demo.service.StatisticheService;
import com.example.demo.service.BackupService;
import com.example.demo.service.ReminderService;
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
    
    @Autowired
    private StatisticheService statisticheService;
    
    @Autowired
    private BackupService backupService;
    
    @Autowired
    private ReminderService reminderService;
    
    @Autowired
    private CandidaturaService candidaturaService;
    
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
        // Dati base per la dashboard
        model.addAttribute("appuntamenti", appuntamentoService.getAllAppuntamenti());
        model.addAttribute("contatti", contattoService.getContattiNonLetti());
        model.addAttribute("lavori", lavoroService.getAllLavori());
        
        // Candidature
        model.addAttribute("candidature", candidaturaService.trovaTutte());
        model.addAttribute("candidatureStats", candidaturaService.getStatistiche());
        
        // NUOVO: Statistiche complete per analytics
        model.addAttribute("stats", statisticheService.getStatisticheDashboard());
        model.addAttribute("statsMensili", statisticheService.getStatisticheMensili(6)); // Ultimi 6 mesi
        
        return "admin/dashboard";
    }
    
    /**
     * Endpoint per eseguire backup manuale on-demand.
     */
    @PostMapping("/backup")
    public String eseguiBackup(RedirectAttributes redirectAttributes) {
        boolean successo = backupService.backupManuale();
        if (successo) {
            redirectAttributes.addFlashAttribute("success", "✅ Backup eseguito con successo!");
        } else {
            redirectAttributes.addFlashAttribute("error", "❌ Errore durante il backup. Controlla i log.");
        }
        return "redirect:/admin/dashboard";
    }
    
    /**
     * Endpoint per inviare reminder manuale a un appuntamento specifico.
     */
    @PostMapping("/appuntamenti/{id}/reminder")
    public String inviaReminder(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean inviato = reminderService.inviaReminderManuale(id);
        if (inviato) {
            redirectAttributes.addFlashAttribute("success", "✅ Reminder inviato al cliente!");
        } else {
            redirectAttributes.addFlashAttribute("error", "⚠️ Impossibile inviare reminder. Verifica che l'appuntamento sia confermato.");
        }
        return "redirect:/admin/appuntamenti";
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
    
    @PostMapping("/appuntamenti/{id}/stato")
    public String aggiornaStatoAppuntamento(@PathVariable Long id, 
                                           @RequestParam("stato") String stato,
                                           RedirectAttributes redirectAttributes) {
        try {
            Appuntamento.StatoAppuntamento nuovoStato = Appuntamento.StatoAppuntamento.valueOf(stato);
            appuntamentoService.aggiornaStato(id, nuovoStato);
            redirectAttributes.addFlashAttribute("success", "Stato appuntamento aggiornato!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'aggiornare lo stato");
        }
        return "redirect:/admin/appuntamenti";
    }
    
    @PostMapping("/appuntamenti/{id}/conferma")
    public String confermaAppuntamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appuntamentoService.confermaAppuntamento(id);
            redirectAttributes.addFlashAttribute("success", "✅ Appuntamento confermato! Email inviata al cliente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nella conferma: " + e.getMessage());
        }
        return "redirect:/admin/appuntamenti";
    }
    
    @PostMapping("/appuntamenti/{id}/rifiuta")
    public String rifiutaAppuntamento(@PathVariable Long id, 
                                      @RequestParam("motivazione") String motivazione,
                                      RedirectAttributes redirectAttributes) {
        try {
            if (motivazione == null || motivazione.trim().isEmpty()) {
                motivazione = "Purtroppo non siamo disponibili in quella fascia oraria. Ti invitiamo a scegliere un'altra data.";
            }
            appuntamentoService.rifiutaAppuntamento(id, motivazione);
            redirectAttributes.addFlashAttribute("success", "⚠️ Appuntamento rifiutato. Email di notifica inviata al cliente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nel rifiuto: " + e.getMessage());
        }
        return "redirect:/admin/appuntamenti";
    }
    
    @PostMapping("/appuntamenti/{id}/elimina")
    public String eliminaAppuntamento(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            appuntamentoService.eliminaAppuntamento(id);
            redirectAttributes.addFlashAttribute("success", "Appuntamento eliminato!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'eliminare l'appuntamento");
        }
        return "redirect:/admin/appuntamenti";
    }
    
    @GetMapping("/appuntamenti/{id}/dettaglio")
    public String dettaglioAppuntamento(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Appuntamento appuntamento = appuntamentoService.getAppuntamentoById(id)
                .orElseThrow(() -> new RuntimeException("Appuntamento non trovato"));
            model.addAttribute("appuntamento", appuntamento);
            return "admin/dettaglio-appuntamento";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Appuntamento non trovato");
            return "redirect:/admin/appuntamenti";
        }
    }

    // Gestione Contatti
    @GetMapping("/contatti")
    public String gestioneContatti(Model model) {
        model.addAttribute("contatti", contattoService.getAllContatti());
        return "admin/contatti";
    }

    // === GESTIONE CANDIDATURE ===
    
    /**
     * Lista tutte le candidature
     */
    @GetMapping("/candidature")
    public String gestioneCandidature(
            @RequestParam(value = "stato", required = false) String stato,
            Model model) {
        
        List<Candidatura> candidature;
        if (stato != null && !stato.isEmpty()) {
            candidature = candidaturaService.trovaPerStato(stato);
        } else {
            candidature = candidaturaService.trovaTutte();
        }
        
        model.addAttribute("candidature", candidature);
        model.addAttribute("stats", candidaturaService.getStatistiche());
        model.addAttribute("statoFiltro", stato);
        
        return "admin/candidature";
    }
    
    /**
     * Dettaglio candidatura
     */
    @GetMapping("/candidature/{id}")
    public String dettaglioCandidatura(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Candidatura candidatura = candidaturaService.trovaPerID(id);
            model.addAttribute("candidatura", candidatura);
            return "admin/dettaglio-candidatura";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Candidatura non trovata");
            return "redirect:/admin/candidature";
        }
    }
    
    /**
     * Cambia stato candidatura
     */
    @PostMapping("/candidature/{id}/stato")
    public String aggiornaStatoCandidatura(
            @PathVariable Long id,
            @RequestParam("stato") String stato,
            @RequestParam(value = "note", required = false) String note,
            RedirectAttributes redirectAttributes) {
        try {
            candidaturaService.aggiornaStato(id, stato, note);
            redirectAttributes.addFlashAttribute("success", "Stato candidatura aggiornato!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'aggiornare lo stato: " + e.getMessage());
        }
        return "redirect:/admin/candidature/" + id;
    }
    
    /**
     * Rispondi a una candidatura
     */
    @PostMapping("/candidature/{id}/rispondi")
    public String rispondiCandidatura(
            @PathVariable Long id,
            @RequestParam("messaggio") String messaggio,
            @RequestParam("stato") String stato,
            RedirectAttributes redirectAttributes) {
        try {
            if (messaggio == null || messaggio.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Il messaggio è obbligatorio");
                return "redirect:/admin/candidature/" + id;
            }
            
            candidaturaService.rispondiCandidatura(id, messaggio, stato);
            redirectAttributes.addFlashAttribute("success", "✅ Risposta inviata al candidato via email!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'invio della risposta: " + e.getMessage());
        }
        return "redirect:/admin/candidature/" + id;
    }
    
    /**
     * Elimina candidatura
     */
    @PostMapping("/candidature/{id}/elimina")
    public String eliminaCandidatura(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            candidaturaService.eliminaCandidatura(id);
            redirectAttributes.addFlashAttribute("success", "Candidatura eliminata!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nell'eliminare la candidatura: " + e.getMessage());
        }
        return "redirect:/admin/candidature";
    }
    
    /**
     * Download CV
     */
    @GetMapping("/candidature/{id}/cv")
    public String downloadCV(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Candidatura candidatura = candidaturaService.trovaPerID(id);
            if (candidatura.getCvFilePath() != null) {
                // Il download sarà gestito direttamente dall'URL del file
                return "redirect:/" + candidatura.getCvFilePath();
            } else {
                redirectAttributes.addFlashAttribute("error", "CV non disponibile");
                return "redirect:/admin/candidature/" + id;
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Errore nel download del CV");
            return "redirect:/admin/candidature";
        }
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