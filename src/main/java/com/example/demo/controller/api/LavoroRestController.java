package com.example.demo.controller.api;

import com.example.demo.model.Lavoro;
import com.example.demo.service.LavoroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST API Controller per la gestione dei Lavori/Portfolio.
 * 
 * Fornisce endpoint JSON per operazioni CRUD sui lavori completati.
 * Utile per integrazioni con app mobile, dashboard esterne, o SPA.
 * 
 * ENDPOINTS:
 * - GET    /api/lavori              → Lista tutti i lavori
 * - GET    /api/lavori/{id}         → Dettaglio singolo lavoro
 * - GET    /api/lavori/visibili     → Solo lavori visibili pubblicamente
 * - GET    /api/lavori/categoria/{cat} → Filtra per categoria
 * - POST   /api/lavori              → Crea nuovo lavoro
 * - PUT    /api/lavori/{id}         → Aggiorna lavoro esistente
 * - DELETE /api/lavori/{id}         → Elimina lavoro
 * - PATCH  /api/lavori/{id}/visibilita → Cambia visibilità
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@RestController
@RequestMapping("/api/lavori")
@CrossOrigin(origins = "*") // Permetti richieste da qualsiasi origine (disabilita in produzione!)
public class LavoroRestController {

    @Autowired
    private LavoroService lavoroService;

    /**
     * GET /api/lavori
     * Recupera tutti i lavori nel database
     * 
     * @return Lista completa di lavori in formato JSON
     */
    @GetMapping
    public ResponseEntity<List<Lavoro>> getAllLavori() {
        List<Lavoro> lavori = lavoroService.getAllLavori();
        return ResponseEntity.ok(lavori);
    }

    /**
     * GET /api/lavori/visibili
     * Recupera solo i lavori visibili pubblicamente
     * 
     * @return Lista lavori con visibile=true
     */
    @GetMapping("/visibili")
    public ResponseEntity<List<Lavoro>> getLavoriVisibili() {
        List<Lavoro> tutti = lavoroService.getAllLavori();
        List<Lavoro> visibili = tutti.stream()
            .filter(Lavoro::isPubblicato)
            .toList();
        return ResponseEntity.ok(visibili);
    }

    /**
     * GET /api/lavori/{id}
     * Recupera un singolo lavoro tramite ID
     * 
     * @param id ID del lavoro
     * @return Lavoro se trovato (200), altrimenti 404
     */
    @GetMapping("/{id}")
    public ResponseEntity<Lavoro> getLavoroById(@PathVariable Long id) {
        Optional<Lavoro> lavoro = lavoroService.getLavoroById(id);
        return lavoro.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/lavori/categoria/{categoria}
     * Filtra lavori per categoria
     * 
     * @param categoria Nome categoria (es: "Ristrutturazione")
     * @return Lista lavori della categoria specificata
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Lavoro>> getLavoriByCategoria(@PathVariable String categoria) {
        List<Lavoro> tutti = lavoroService.getAllLavori();
        List<Lavoro> filtrati = tutti.stream()
            .filter(l -> categoria.equalsIgnoreCase(l.getCategoria()))
            .toList();
        return ResponseEntity.ok(filtrati);
    }

    /**
     * POST /api/lavori
     * Crea un nuovo lavoro
     * 
     * Richiede autenticazione admin (da implementare con Spring Security).
     * 
     * Body JSON esempio:
     * {
     *   "titolo": "Ristrutturazione Villa",
     *   "descrizione": "Completa ristrutturazione...",
     *   "categoria": "Ristrutturazione",
     *   "dataInizio": "2024-01-15",
     *   "dataFine": "2024-03-20",
     *   "visibile": true
     * }
     * 
     * @param lavoro Dati del lavoro da creare
     * @return Lavoro creato con ID (201 Created)
     */
    @PostMapping
    public ResponseEntity<Lavoro> creaLavoro(@RequestBody Lavoro lavoro) {
        try {
            Lavoro nuovoLavoro = lavoroService.salvaLavoro(lavoro);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuovoLavoro);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    /**
     * PUT /api/lavori/{id}
     * Aggiorna un lavoro esistente
     * 
     * @param id ID del lavoro da aggiornare
     * @param lavoroAggiornato Nuovi dati del lavoro
     * @return Lavoro aggiornato (200) o 404 se non trovato
     */
    @PutMapping("/{id}")
    public ResponseEntity<Lavoro> aggiornaLavoro(@PathVariable Long id, @RequestBody Lavoro lavoroAggiornato) {
        Optional<Lavoro> lavoroEsistente = lavoroService.getLavoroById(id);
        
        if (lavoroEsistente.isPresent()) {
            lavoroAggiornato.setId(id);
            Lavoro lavoro = lavoroService.salvaLavoro(lavoroAggiornato);
            return ResponseEntity.ok(lavoro);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * DELETE /api/lavori/{id}
     * Elimina un lavoro dal database
     * 
     * @param id ID del lavoro da eliminare
     * @return 204 No Content se eliminato, 404 se non trovato
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminaLavoro(@PathVariable Long id) {
        Optional<Lavoro> lavoro = lavoroService.getLavoroById(id);
        
        if (lavoro.isPresent()) {
            lavoroService.eliminaLavoro(id);
            return ResponseEntity.noContent().build();
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * PATCH /api/lavori/{id}/visibilita
     * Cambia la visibilità di un lavoro (pubblica/nascosta)
     * 
     * Body JSON: { "visibile": true }
     * 
     * @param id ID del lavoro
     * @param payload Map contenente il campo "visibile"
     * @return Lavoro aggiornato
     */
    @PatchMapping("/{id}/visibilita")
    public ResponseEntity<Lavoro> cambiaVisibilita(@PathVariable Long id, @RequestBody Map<String, Boolean> payload) {
        Optional<Lavoro> lavoroOpt = lavoroService.getLavoroById(id);
        
        if (lavoroOpt.isPresent()) {
            Lavoro lavoro = lavoroOpt.get();
            boolean visibile = payload.get("visibile");
            lavoro.setPubblicato(visibile);
            Lavoro aggiornato = lavoroService.salvaLavoro(lavoro);
            return ResponseEntity.ok(aggiornato);
        }
        
        return ResponseEntity.notFound().build();
    }

    /**
     * GET /api/lavori/stats
     * Statistiche sui lavori
     * 
     * @return Map con conteggi e statistiche
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistiche() {
        Map<String, Object> stats = new HashMap<>();
        List<Lavoro> tuttiLavori = lavoroService.getAllLavori();
        long lavoriVisibili = tuttiLavori.stream().filter(Lavoro::isPubblicato).count();
        
        stats.put("totale", tuttiLavori.size());
        stats.put("visibili", lavoriVisibili);
        stats.put("nascosti", tuttiLavori.size() - lavoriVisibili);
        
        // Conta per categoria
        Map<String, Long> perCategoria = new HashMap<>();
        tuttiLavori.forEach(l -> {
            String cat = l.getCategoria();
            perCategoria.put(cat, perCategoria.getOrDefault(cat, 0L) + 1);
        });
        stats.put("perCategoria", perCategoria);
        
        return ResponseEntity.ok(stats);
    }
}
