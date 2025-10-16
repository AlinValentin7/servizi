package com.example.demo.controller.api;

import com.example.demo.service.StatisticheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller per Statistiche e Dashboard.
 * 
 * Fornisce dati aggregati per dashboard admin, grafici e reportistica.
 * 
 * ENDPOINTS:
 * - GET /api/stats/dashboard           → Tutti i dati per dashboard principale
 * - GET /api/stats/appuntamenti        → Statistiche appuntamenti
 * - GET /api/stats/contatti            → Statistiche messaggi
 * - GET /api/stats/lavori              → Statistiche portfolio
 * - GET /api/stats/conversione         → Tasso di conversione visite → appuntamenti
 * - GET /api/stats/mese                → Statistiche del mese corrente
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@RestController
@RequestMapping("/api/stats")
@CrossOrigin(origins = "*")
public class StatisticheRestController {

    @Autowired
    private StatisticheService statisticheService;

    /**
     * GET /api/stats/dashboard
     * Recupera tutte le statistiche per la dashboard admin
     * 
     * Ritorna un JSON con:
     * - Totale appuntamenti (in attesa, confermati, completati)
     * - Totale contatti (letti, non letti)
     * - Totale lavori (visibili, nascosti)
     * - Appuntamenti questa settimana
     * - Appuntamenti questo mese
     * 
     * @return Map completa con tutte le statistiche
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = statisticheService.getStatisticheDashboard();
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/appuntamenti
     * Statistiche dettagliate sugli appuntamenti
     * 
     * @return Map con metriche sugli appuntamenti
     */
    @GetMapping("/appuntamenti")
    public ResponseEntity<Map<String, Object>> getAppuntamentiStats() {
        // Usa la dashboard per ottenere le statistiche
        Map<String, Object> dashboard = statisticheService.getStatisticheDashboard();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totale", dashboard.get("appuntamentiTotali"));
        stats.put("inAttesa", dashboard.get("appuntamentiInAttesa"));
        stats.put("confermati", dashboard.get("appuntamentiConfermati"));
        stats.put("completati", dashboard.get("appuntamentiCompletati"));
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/contatti
     * Statistiche dettagliate sui messaggi di contatto
     * 
     * @return Map con metriche sui contatti
     */
    @GetMapping("/contatti")
    public ResponseEntity<Map<String, Object>> getContattiStats() {
        Map<String, Object> dashboard = statisticheService.getStatisticheDashboard();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totale", dashboard.get("contattiTotali"));
        stats.put("nonLetti", dashboard.get("contattiNonLetti"));
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/lavori
     * Statistiche dettagliate sul portfolio lavori
     * 
     * @return Map con metriche sui lavori
     */
    @GetMapping("/lavori")
    public ResponseEntity<Map<String, Object>> getLavoriStats() {
        Map<String, Object> dashboard = statisticheService.getStatisticheDashboard();
        Map<String, Object> stats = new HashMap<>();
        stats.put("totale", dashboard.get("lavoriTotali"));
        stats.put("visibili", dashboard.get("lavoriVisibili"));
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/mese
     * Statistiche del mese corrente
     * 
     * @return Map con dati del mese (appuntamenti, contatti nuovi, ecc.)
     */
    @GetMapping("/mese")
    public ResponseEntity<Map<String, Object>> getStatsMeseCorrente() {
        Map<String, Object> dashboard = statisticheService.getStatisticheDashboard();
        Map<String, Object> stats = new HashMap<>();
        stats.put("appuntamentiMese", dashboard.get("appuntamentiQuestoMese"));
        stats.put("appuntamentiSettimana", dashboard.get("appuntamentiQuestaSettimana"));
        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/stats/riepilogo
     * Riepilogo sintetico per widget/notifiche
     * 
     * @return Map con i numeri più importanti
     */
    @GetMapping("/riepilogo")
    public ResponseEntity<Map<String, Object>> getRiepilogo() {
        Map<String, Object> riepilogo = new HashMap<>();
        Map<String, Object> dashboard = statisticheService.getStatisticheDashboard();
        
        // Estrai solo i dati essenziali
        riepilogo.put("appuntamentiInAttesa", dashboard.get("appuntamentiInAttesa"));
        riepilogo.put("contattiNonLetti", dashboard.get("contattiNonLetti"));
        riepilogo.put("appuntamentiQuestaMese", dashboard.get("appuntamentiQuestoMese"));
        
        return ResponseEntity.ok(riepilogo);
    }

    /**
     * GET /api/health
     * Health check dell'applicazione
     * 
     * @return Status dell'applicazione
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("application", "Servizi Edili Elvis SRL");
        health.put("version", "1.0.0");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}
