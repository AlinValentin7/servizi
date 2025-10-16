package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.model.Contatto;
import com.example.demo.model.Lavoro;
import com.example.demo.repository.AppuntamentoRepository;
import com.example.demo.repository.ContattoRepository;
import com.example.demo.repository.LavoroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service per statistiche e analytics del business.
 * 
 * METRICHE CALCOLATE:
 * - Appuntamenti per stato (quanti confermati/rifiutati/completati)
 * - Trend mensili (crescita o calo richieste)
 * - Tasso di conversione (% appuntamenti confermati)
 * - Servizi più richiesti (quali lavori vanno di più)
 * - Motivi di rifiuto più comuni (per migliorare servizio)
 * - Media tempo risposta contatti
 * 
 * UTILIZZO:
 * Mostra queste statistiche nella dashboard admin per prendere
 * decisioni di business informate.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class StatisticheService {
    
    @Autowired
    private AppuntamentoRepository appuntamentoRepository;
    
    @Autowired
    private ContattoRepository contattoRepository;
    
    @Autowired
    private LavoroRepository lavoroRepository;
    
    /**
     * Calcola statistiche complete per la dashboard admin.
     * 
     * @return Mappa con tutte le statistiche chiave
     */
    public Map<String, Object> getStatisticheDashboard() {
        Map<String, Object> stats = new HashMap<>();
        
        // STATISTICHE APPUNTAMENTI
        List<Appuntamento> tuttiAppuntamenti = appuntamentoRepository.findAll();
        stats.put("totaleAppuntamenti", tuttiAppuntamenti.size());
        stats.put("appuntamentiInAttesa", contaPerStato(tuttiAppuntamenti, Appuntamento.StatoAppuntamento.IN_ATTESA));
        stats.put("appuntamentiConfermati", contaPerStato(tuttiAppuntamenti, Appuntamento.StatoAppuntamento.CONFERMATO));
        stats.put("appuntamentiCompletati", contaPerStato(tuttiAppuntamenti, Appuntamento.StatoAppuntamento.COMPLETATO));
        stats.put("appuntamentiAnnullati", contaPerStato(tuttiAppuntamenti, Appuntamento.StatoAppuntamento.ANNULLATO));
        
        // TASSO DI CONVERSIONE (% appuntamenti confermati sul totale richieste)
        long richieste = tuttiAppuntamenti.stream()
            .filter(a -> a.getStato() != Appuntamento.StatoAppuntamento.IN_ATTESA)
            .count();
        long confermati = contaPerStato(tuttiAppuntamenti, Appuntamento.StatoAppuntamento.CONFERMATO);
        long completati = contaPerStato(tuttiAppuntamenti, Appuntamento.StatoAppuntamento.COMPLETATO);
        double tassoConversione = richieste > 0 ? ((confermati + completati) * 100.0 / richieste) : 0;
        stats.put("tassoConversione", Math.round(tassoConversione * 100.0) / 100.0);
        
        // SERVIZI PIÙ RICHIESTI (top 5)
        Map<String, Long> serviziRichiesti = tuttiAppuntamenti.stream()
            .collect(Collectors.groupingBy(Appuntamento::getTipoServizio, Collectors.counting()));
        List<Map.Entry<String, Long>> topServizi = serviziRichiesti.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
        stats.put("topServizi", topServizi);
        
        // STATISTICHE CONTATTI
        List<Contatto> tuttiContatti = contattoRepository.findAllByOrderByDataInvioDesc();
        stats.put("totaleContatti", tuttiContatti.size());
        stats.put("contattiNonLetti", tuttiContatti.stream().filter(c -> !c.isLetto()).count());
        
        // STATISTICHE PORTFOLIO
        List<Lavoro> tuttiLavori = lavoroRepository.findAll();
        stats.put("totaleLavori", tuttiLavori.size());
        stats.put("lavoriPubblicati", tuttiLavori.stream().filter(Lavoro::isPubblicato).count());
        
        // TREND ULTIMO MESE (confronto con mese precedente)
        LocalDateTime inizioMeseCorrente = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime inizioMesePrecedente = YearMonth.now().minusMonths(1).atDay(1).atStartOfDay();
        
        long appuntamentiMeseCorrente = tuttiAppuntamenti.stream()
            .filter(a -> a.getDataCreazione().isAfter(inizioMeseCorrente))
            .count();
        long appuntamentiMesePrecedente = tuttiAppuntamenti.stream()
            .filter(a -> a.getDataCreazione().isAfter(inizioMesePrecedente) 
                      && a.getDataCreazione().isBefore(inizioMeseCorrente))
            .count();
        
        double crescitaPercentuale = appuntamentiMesePrecedente > 0 
            ? ((appuntamentiMeseCorrente - appuntamentiMesePrecedente) * 100.0 / appuntamentiMesePrecedente)
            : 0;
        stats.put("crescitaMensile", Math.round(crescitaPercentuale * 100.0) / 100.0);
        
        // MOTIVI DI RIFIUTO PIÙ COMUNI (per migliorare il servizio)
        List<Appuntamento> rifiutati = tuttiAppuntamenti.stream()
            .filter(a -> a.getStato() == Appuntamento.StatoAppuntamento.ANNULLATO)
            .filter(a -> a.getMotivazioneRifiuto() != null)
            .collect(Collectors.toList());
        stats.put("totaleRifiutati", rifiutati.size());
        
        return stats;
    }
    
    /**
     * Calcola statistiche mensili per grafici trend.
     * 
     * @param numeroMesi Quanti mesi indietro guardare
     * @return Lista di statistiche per ogni mese
     */
    public List<Map<String, Object>> getStatisticheMensili(int numeroMesi) {
        List<Map<String, Object>> statsMensili = new ArrayList<>();
        List<Appuntamento> tuttiAppuntamenti = appuntamentoRepository.findAll();
        
        for (int i = numeroMesi - 1; i >= 0; i--) {
            YearMonth mese = YearMonth.now().minusMonths(i);
            LocalDateTime inizioMese = mese.atDay(1).atStartOfDay();
            LocalDateTime fineMese = mese.atEndOfMonth().atTime(23, 59, 59);
            
            List<Appuntamento> appuntamentiMese = tuttiAppuntamenti.stream()
                .filter(a -> a.getDataCreazione().isAfter(inizioMese) 
                          && a.getDataCreazione().isBefore(fineMese))
                .collect(Collectors.toList());
            
            Map<String, Object> statMese = new HashMap<>();
            statMese.put("mese", mese.toString());
            statMese.put("totale", appuntamentiMese.size());
            statMese.put("confermati", contaPerStato(appuntamentiMese, Appuntamento.StatoAppuntamento.CONFERMATO));
            statMese.put("annullati", contaPerStato(appuntamentiMese, Appuntamento.StatoAppuntamento.ANNULLATO));
            
            statsMensili.add(statMese);
        }
        
        return statsMensili;
    }
    
    /**
     * Helper: conta appuntamenti per stato specifico.
     */
    private long contaPerStato(List<Appuntamento> appuntamenti, Appuntamento.StatoAppuntamento stato) {
        return appuntamenti.stream()
            .filter(a -> a.getStato() == stato)
            .count();
    }
}
