package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.repository.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
    
    public Appuntamento creaAppuntamento(Appuntamento appuntamento) {
        Appuntamento saved = appuntamentoRepository.save(appuntamento);
        
        // Invia email di conferma (opzionale - non blocca se fallisce)
        try {
            emailService.inviaEmailConfermaAppuntamento(saved);
        } catch (Exception e) {
            System.out.println("Avviso: Impossibile inviare email di conferma. " + e.getMessage());
            // L'appuntamento è comunque salvato, l'email è opzionale
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
    
    public void eliminaAppuntamento(Long id) {
        appuntamentoRepository.deleteById(id);
    }
}