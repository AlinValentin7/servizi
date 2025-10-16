package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.repository.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AppuntamentoService {
    
    @Autowired
    private AppuntamentoRepository appuntamentoRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private WhatsAppService whatsAppService;
    
    /**
     * Verifica se lo slot orario è disponibile (durata 1 ora)
     * Controlla sovrapposizioni con appuntamenti confermati o in attesa
     */
    public boolean isSlotDisponibile(LocalDateTime dataOraRichiesta) {
        LocalDateTime inizioSlot = dataOraRichiesta;
        LocalDateTime fineSlot = dataOraRichiesta.plusHours(1);
        
        List<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        
        for (Appuntamento app : appuntamenti) {
            // Ignora appuntamenti annullati
            if (app.getStato() == Appuntamento.StatoAppuntamento.ANNULLATO) {
                continue;
            }
            
            LocalDateTime inizioEsistente = app.getDataAppuntamento();
            LocalDateTime fineEsistente = inizioEsistente.plusHours(1);
            
            // Verifica sovrapposizione: nuovo inizia prima che finisca esistente E nuovo finisce dopo che inizia esistente
            boolean sovrapposizione = inizioSlot.isBefore(fineEsistente) && fineSlot.isAfter(inizioEsistente);
            
            if (sovrapposizione) {
                return false; // Slot non disponibile
            }
        }
        
        return true; // Slot disponibile
    }
    
    public Appuntamento creaAppuntamento(Appuntamento appuntamento) {
        // Verifica disponibilità slot prima di salvare
        if (!isSlotDisponibile(appuntamento.getDataAppuntamento())) {
            throw new IllegalStateException("FASCIA_ORARIA_NON_DISPONIBILE");
        }
        
        Appuntamento saved = appuntamentoRepository.save(appuntamento);
        
        // Invia email di conferma (opzionale - non blocca se fallisce)
        try {
            emailService.inviaEmailConfermaAppuntamento(saved);
        } catch (Exception e) {
            System.out.println("Avviso: Impossibile inviare email di conferma. " + e.getMessage());
        }
        
        // Invia notifica WhatsApp (opzionale)
        try {
            whatsAppService.inviaNotificaAppuntamento(saved);
        } catch (Exception e) {
            System.out.println("Avviso: Impossibile inviare notifica WhatsApp. " + e.getMessage());
        }
        
        return saved;
    }
    
    public List<Appuntamento> getAllAppuntamenti() {
        return appuntamentoRepository.findAll();
    }
    
    public Optional<Appuntamento> getAppuntamentoById(Long id) {
        return appuntamentoRepository.findById(id);
    }
    
    public List<Appuntamento> getAppuntamentiByStato(Appuntamento.StatoAppuntamento stato) {
        return appuntamentoRepository.findByStatoOrderByDataAppuntamentoAsc(stato);
    }
    
    public Appuntamento aggiornaStato(Long id, Appuntamento.StatoAppuntamento nuovoStato) {
        Optional<Appuntamento> appuntamento = appuntamentoRepository.findById(id);
        if (appuntamento.isPresent()) {
            Appuntamento app = appuntamento.get();
            app.setStato(nuovoStato);
            return appuntamentoRepository.save(app);
        }
        return null;
    }
    
    /**
     * Conferma appuntamento e invia email di conferma al cliente
     */
    public void confermaAppuntamento(Long id) {
        Optional<Appuntamento> appuntamentoOpt = appuntamentoRepository.findById(id);
        if (appuntamentoOpt.isPresent()) {
            Appuntamento app = appuntamentoOpt.get();
            app.setStato(Appuntamento.StatoAppuntamento.CONFERMATO);
            appuntamentoRepository.save(app);
            
            try {
                emailService.inviaEmailConfermaAppuntamentoDaAdmin(app);
            } catch (Exception e) {
                System.err.println("Errore invio email conferma appuntamento: " + e.getMessage());
                throw new RuntimeException("Impossibile inviare email di conferma");
            }
        }
    }
    
    /**
     * Rifiuta appuntamento per indisponibilità e invia email al cliente
     */
    public void rifiutaAppuntamento(Long id, String motivazione) {
        Optional<Appuntamento> appuntamentoOpt = appuntamentoRepository.findById(id);
        if (appuntamentoOpt.isPresent()) {
            Appuntamento app = appuntamentoOpt.get();
            app.setStato(Appuntamento.StatoAppuntamento.ANNULLATO);
            app.setMotivazioneRifiuto(motivazione);
            app.setDataAnnullamento(LocalDateTime.now());
            appuntamentoRepository.save(app);
            
            try {
                emailService.inviaEmailRifiutoAppuntamento(app, motivazione);
            } catch (Exception e) {
                System.err.println("Errore invio email rifiuto appuntamento: " + e.getMessage());
                throw new RuntimeException("Impossibile inviare email di rifiuto");
            }
        }
    }
    
    public void eliminaAppuntamento(Long id) {
        appuntamentoRepository.deleteById(id);
    }
}