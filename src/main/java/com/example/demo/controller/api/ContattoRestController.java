package com.example.demo.controller.api;

import com.example.demo.model.Contatto;
import com.example.demo.service.ContattoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller per la gestione dei Contatti/Messaggi.
 * 
 * ENDPOINTS:
 * - GET    /api/contatti              → Lista tutti i contatti
 * - GET    /api/contatti/{id}         → Dettaglio singolo contatto
 * - GET    /api/contatti/non-letti    → Solo messaggi non letti
 * - POST   /api/contatti              → Crea nuovo contatto (form pubblico)
 * - PATCH  /api/contatti/{id}/letto   → Segna come letto
 * - POST   /api/contatti/{id}/rispondi → Invia risposta via email
 * - DELETE /api/contatti/{id}         → Elimina contatto
 * - GET    /api/contatti/stats        → Statistiche messaggi
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@RestController
@RequestMapping("/api/contatti")
@CrossOrigin(origins = "*")
public class ContattoRestController {

    @Autowired
    private ContattoService contattoService;

    /**
     * GET /api/contatti
     * Recupera tutti i messaggi di contatto
     * 
     * @return Lista completa dei contatti
     */
    @GetMapping
    public ResponseEntity<List<Contatto>> getAllContatti() {
        List<Contatto> contatti = contattoService.getAllContatti();
        return ResponseEntity.ok(contatti);
    }

    /**
     * GET /api/contatti/non-letti
     * Recupera solo i messaggi non ancora letti
     * 
     * @return Lista contatti con letto=false
     */
    @GetMapping("/non-letti")
    public ResponseEntity<List<Contatto>> getContattiNonLetti() {
        List<Contatto> contatti = contattoService.getContattiNonLetti();
        return ResponseEntity.ok(contatti);
    }

    /**
     * GET /api/contatti/{id}
     * Recupera un singolo messaggio di contatto
     * 
     * @param id ID del contatto
     * @return Contatto se trovato, altrimenti 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contatto> getContattoById(@PathVariable Long id) {
        Optional<Contatto> contatto = contattoService.getContattoById(id);
        return contatto.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/contatti
     * Crea un nuovo messaggio di contatto (pubblico, no auth richiesta)
     * 
     * Body JSON esempio:
     * {
     *   "nome": "Mario Rossi",
     *   "email": "mario@example.com",
     *   "telefono": "+39 333 1234567",
     *   "messaggio": "Vorrei un preventivo per..."
     * }
     * 
     * @param contatto Dati del contatto
     * @return Contatto salvato con conferma
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> creaContatto(@RequestBody Contatto contatto) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validazione base
            if (contatto.getNome() == null || contatto.getNome().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Il nome è obbligatorio");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (contatto.getEmail() == null || !contatto.getEmail().contains("@")) {
                response.put("success", false);
                response.put("message", "Email non valida");
                return ResponseEntity.badRequest().body(response);
            }
            
            if (contatto.getMessaggio() == null || contatto.getMessaggio().trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "Il messaggio è obbligatorio");
                return ResponseEntity.badRequest().body(response);
            }
            
            Contatto salvato = contattoService.salvaContatto(contatto);
            response.put("success", true);
            response.put("message", "Messaggio inviato con successo! Ti risponderemo al più presto.");
            response.put("contatto", salvato);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Errore durante l'invio del messaggio");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * PATCH /api/contatti/{id}/letto
     * Segna un contatto come letto
     * 
     * @param id ID del contatto
     * @return Contatto aggiornato
     */
    @PatchMapping("/{id}/letto")
    public ResponseEntity<Contatto> segnaComeLetto(@PathVariable Long id) {
        Contatto contatto = contattoService.segnaComeLetto(id);
        
        if (contatto != null) {
            return ResponseEntity.ok(contatto);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * POST /api/contatti/{id}/rispondi
     * Invia una risposta via email al cliente
     * 
     * Body JSON: { "risposta": "Grazie per averci contattato..." }
     * 
     * @param id ID del contatto
     * @param payload Map con il testo della risposta
     * @return Conferma invio
     */
    @PostMapping("/{id}/rispondi")
    public ResponseEntity<Map<String, Object>> rispondiContatto(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        
        Map<String, Object> response = new HashMap<>();
        String risposta = payload.get("risposta");
        
        if (risposta == null || risposta.trim().isEmpty()) {
            response.put("success", false);
            response.put("message", "La risposta non può essere vuota");
            return ResponseEntity.badRequest().body(response);
        }
        
        Optional<Contatto> contattoOpt = contattoService.getContattoById(id);
        
        if (contattoOpt.isEmpty()) {
            response.put("success", false);
            response.put("message", "Contatto non trovato");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        try {
            contattoService.rispondiContatto(id, risposta);
            response.put("success", true);
            response.put("message", "Risposta inviata con successo");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Errore durante l'invio della risposta");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * DELETE /api/contatti/{id}
     * Elimina un contatto dal database
     * 
     * @param id ID del contatto
     * @return 204 No Content se eliminato, 404 se non trovato
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminaContatto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Optional<Contatto> contatto = contattoService.getContattoById(id);
        
        if (contatto.isPresent()) {
            contattoService.eliminaContatto(id);
            response.put("success", true);
            response.put("message", "Contatto eliminato con successo");
            return ResponseEntity.ok(response);
        }
        
        response.put("success", false);
        response.put("message", "Contatto non trovato");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * GET /api/contatti/stats
     * Statistiche sui messaggi di contatto
     * 
     * @return Map con conteggi e metriche
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistiche() {
        Map<String, Object> stats = new HashMap<>();
        List<Contatto> tutti = contattoService.getAllContatti();
        List<Contatto> nonLetti = contattoService.getContattiNonLetti();
        
        stats.put("totale", tutti.size());
        stats.put("nonLetti", nonLetti.size());
        stats.put("letti", tutti.size() - nonLetti.size());
        
        // Percentuale risposta
        if (tutti.size() > 0) {
            double percentualeLetti = ((double) (tutti.size() - nonLetti.size()) / tutti.size()) * 100;
            stats.put("percentualeLetti", String.format("%.1f%%", percentualeLetti));
        } else {
            stats.put("percentualeLetti", "0%");
        }
        
        return ResponseEntity.ok(stats);
    }
}

