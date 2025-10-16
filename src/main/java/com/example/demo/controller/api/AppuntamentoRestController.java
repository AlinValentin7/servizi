package com.example.demo.controller.api;

import com.example.demo.model.Appuntamento;
import com.example.demo.service.AppuntamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller per la gestione degli Appuntamenti.
 * 
 * ENDPOINTS:
 * - GET    /api/appuntamenti                → Lista tutti gli appuntamenti
 * - GET    /api/appuntamenti/{id}           → Dettaglio singolo appuntamento
 * - GET    /api/appuntamenti/stato/{stato}  → Filtra per stato (IN_ATTESA, CONFERMATO, ecc.)
 * - GET    /api/appuntamenti/data/{data}    → Appuntamenti per una data specifica
 * - GET    /api/appuntamenti/disponibilita  → Controlla disponibilità orario
 * - POST   /api/appuntamenti                → Crea nuovo appuntamento
 * - PUT    /api/appuntamenti/{id}           → Aggiorna appuntamento
 * - DELETE /api/appuntamenti/{id}           → Elimina appuntamento
 * - PATCH  /api/appuntamenti/{id}/stato     → Cambia stato appuntamento
 * - GET    /api/appuntamenti/stats          → Statistiche appuntamenti
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@RestController
@RequestMapping("/api/appuntamenti")
@CrossOrigin(origins = "*")
public class AppuntamentoRestController {

    @Autowired
    private AppuntamentoService appuntamentoService;

    /**
     * GET /api/appuntamenti
     * Recupera tutti gli appuntamenti
     * 
     * @return Lista completa degli appuntamenti
     */
    @GetMapping
    public ResponseEntity<List<Appuntamento>> getAllAppuntamenti() {
        List<Appuntamento> appuntamenti = appuntamentoService.getAllAppuntamenti();
        return ResponseEntity.ok(appuntamenti);
    }

    /**
     * GET /api/appuntamenti/{id}
     * Recupera un singolo appuntamento tramite ID
     * 
     * @param id ID dell'appuntamento
     * @return Appuntamento se trovato (200), altrimenti 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Appuntamento> getAppuntamentoById(@PathVariable Long id) {
        Optional<Appuntamento> appuntamento = appuntamentoService.getAppuntamentoById(id);
        return appuntamento.map(ResponseEntity::ok)
                          .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/appuntamenti/stato/{stato}
     * Filtra appuntamenti per stato
     * 
     * @param stato Stato appuntamento (IN_ATTESA, CONFERMATO, COMPLETATO, ANNULLATO)
     * @return Lista appuntamenti con lo stato specificato
     */
    @GetMapping("/stato/{stato}")
    public ResponseEntity<List<Appuntamento>> getAppuntamentiByStato(@PathVariable String stato) {
        try {
            Appuntamento.StatoAppuntamento statoEnum = Appuntamento.StatoAppuntamento.valueOf(stato);
            List<Appuntamento> appuntamenti = appuntamentoService.getAppuntamentiByStato(statoEnum);
            return ResponseEntity.ok(appuntamenti);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * GET /api/appuntamenti/data/{data}
     * Recupera tutti gli appuntamenti per una data specifica
     * 
     * @param data Data nel formato yyyy-MM-dd (es: 2025-01-15)
     * @return Lista appuntamenti della data specificata
     */
    @GetMapping("/data/{data}")
    public ResponseEntity<List<Appuntamento>> getAppuntamentiByData(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        List<Appuntamento> tutti = appuntamentoService.getAllAppuntamenti();
        List<Appuntamento> filtrati = tutti.stream()
            .filter(app -> app.getDataAppuntamento().toLocalDate().equals(data))
            .toList();
        return ResponseEntity.ok(filtrati);
    }

    /**
     * GET /api/appuntamenti/disponibilita
     * Controlla se una fascia oraria è disponibile
     * 
     * Query params:
     * - dataOra: Data e ora nel formato ISO (es: 2025-01-15T10:00:00)
     * 
     * Esempio: /api/appuntamenti/disponibilita?dataOra=2025-01-15T10:00:00
     * 
     * @param dataOra Data e ora da verificare
     * @return JSON con campo "disponibile": true/false
     */
    @GetMapping("/disponibilita")
    public ResponseEntity<Map<String, Boolean>> checkDisponibilita(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataOra) {
        boolean disponibile = appuntamentoService.isSlotDisponibile(dataOra);
        Map<String, Boolean> response = new HashMap<>();
        response.put("disponibile", disponibile);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/appuntamenti
     * Crea un nuovo appuntamento
     * 
     * Body JSON esempio:
     * {
     *   "nomeCliente": "Mario Rossi",
     *   "email": "mario@example.com",
     *   "telefono": "+39 333 1234567",
     *   "dataOra": "2025-01-15T10:00:00",
     *   "tipoServizio": "Preventivo Ristrutturazione",
     *   "note": "Interessato a ristrutturazione bagno"
     * }
     * 
     * @param appuntamento Dati dell'appuntamento
     * @return Appuntamento creato (201) o errore (400)
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> creaAppuntamento(@RequestBody Appuntamento appuntamento) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Appuntamento nuovoAppuntamento = appuntamentoService.creaAppuntamento(appuntamento);
            response.put("success", true);
            response.put("message", "Appuntamento creato con successo");
            response.put("appuntamento", nuovoAppuntamento);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", "Fascia oraria non disponibile");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Errore durante la creazione dell'appuntamento");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * PUT /api/appuntamenti/{id}
     * Aggiorna un appuntamento esistente
     * 
     * @param id ID dell'appuntamento da aggiornare
     * @param appuntamentoAggiornato Nuovi dati
     * @return Appuntamento aggiornato o 404
     */
    @PutMapping("/{id}")
    public ResponseEntity<Appuntamento> aggiornaAppuntamento(
            @PathVariable Long id, 
            @RequestBody Appuntamento appuntamentoAggiornato) {
        
        Optional<Appuntamento> esistente = appuntamentoService.getAppuntamentoById(id);
        
        if (esistente.isPresent()) {
            // Aggiorna i campi dell'appuntamento esistente
            Appuntamento app = esistente.get();
            if (appuntamentoAggiornato.getNomeCliente() != null) app.setNomeCliente(appuntamentoAggiornato.getNomeCliente());
            if (appuntamentoAggiornato.getEmail() != null) app.setEmail(appuntamentoAggiornato.getEmail());
            if (appuntamentoAggiornato.getTelefono() != null) app.setTelefono(appuntamentoAggiornato.getTelefono());
            if (appuntamentoAggiornato.getDescrizione() != null) app.setDescrizione(appuntamentoAggiornato.getDescrizione());
            if (appuntamentoAggiornato.getStato() != null) app.setStato(appuntamentoAggiornato.getStato());
            
            Appuntamento aggiornato = appuntamentoService.creaAppuntamento(app);
            return ResponseEntity.ok(aggiornato);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * PATCH /api/appuntamenti/{id}/stato
     * Cambia lo stato di un appuntamento
     * 
     * Body JSON: { "stato": "CONFERMATO" }
     * 
     * @param id ID dell'appuntamento
     * @param payload Map contenente il nuovo stato
     * @return Appuntamento aggiornato
     */
    @PatchMapping("/{id}/stato")
    public ResponseEntity<Appuntamento> cambiaStato(
            @PathVariable Long id, 
            @RequestBody Map<String, String> payload) {
        
        try {
            String statoString = payload.get("stato");
            Appuntamento.StatoAppuntamento nuovoStato = Appuntamento.StatoAppuntamento.valueOf(statoString);
            Appuntamento aggiornato = appuntamentoService.aggiornaStato(id, nuovoStato);
            
            if (aggiornato != null) {
                return ResponseEntity.ok(aggiornato);
            }
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * DELETE /api/appuntamenti/{id}
     * Elimina un appuntamento (o lo annulla)
     * 
     * @param id ID dell'appuntamento
     * @return 204 No Content se eliminato, 404 se non trovato
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaAppuntamento(@PathVariable Long id) {
        Optional<Appuntamento> appuntamento = appuntamentoService.getAppuntamentoById(id);
        
        if (appuntamento.isPresent()) {
            appuntamentoService.eliminaAppuntamento(id);
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /api/appuntamenti/stats
     * Statistiche sugli appuntamenti
     * 
     * @return Map con conteggi per stato e altre metriche
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistiche() {
        Map<String, Object> stats = new HashMap<>();
        List<Appuntamento> tutti = appuntamentoService.getAllAppuntamenti();
        
        stats.put("totale", tutti.size());
        stats.put("inAttesa", appuntamentoService.getAppuntamentiByStato(Appuntamento.StatoAppuntamento.IN_ATTESA).size());
        stats.put("confermati", appuntamentoService.getAppuntamentiByStato(Appuntamento.StatoAppuntamento.CONFERMATO).size());
        stats.put("completati", appuntamentoService.getAppuntamentiByStato(Appuntamento.StatoAppuntamento.COMPLETATO).size());
        stats.put("annullati", appuntamentoService.getAppuntamentiByStato(Appuntamento.StatoAppuntamento.ANNULLATO).size());
        
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/appuntamenti/prossimi
     * Recupera i prossimi appuntamenti (futuri)
     * 
     * @return Lista appuntamenti futuri ordinati per data
     */
    @GetMapping("/prossimi")
    public ResponseEntity<List<Appuntamento>> getProssimiAppuntamenti() {
        List<Appuntamento> tutti = appuntamentoService.getAllAppuntamenti();
        LocalDateTime ora = LocalDateTime.now();
        List<Appuntamento> prossimi = tutti.stream()
            .filter(app -> app.getDataAppuntamento().isAfter(ora))
            .filter(app -> app.getStato() != Appuntamento.StatoAppuntamento.ANNULLATO)
            .sorted((a, b) -> a.getDataAppuntamento().compareTo(b.getDataAppuntamento()))
            .toList();
        return ResponseEntity.ok(prossimi);
    }
}
