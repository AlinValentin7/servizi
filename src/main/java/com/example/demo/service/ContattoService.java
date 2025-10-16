package com.example.demo.service;

import com.example.demo.model.Contatto;
import com.example.demo.repository.ContattoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione dei Contatti ricevuti dal sito web.
 * 
 * Questo servizio gestisce tutte le operazioni relative ai messaggi di contatto
 * inviati dai clienti attraverso il form "Contatti" del sito pubblico.
 * Include funzionalità per:
 * - Salvare nuovi contatti nel database
 * - Recuperare l'elenco di tutti i contatti
 * - Filtrare i contatti non letti (per notifiche admin)
 * - Segnare contatti come letti
 * - Inviare risposte via email ai clienti
 * - Eliminare contatti dal database
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class ContattoService {
    
    // Logger per tracciare le operazioni e eventuali errori
    private static final Logger logger = LoggerFactory.getLogger(ContattoService.class);
    
    // Repository per l'accesso ai dati dei contatti nel database
    @Autowired
    private ContattoRepository contattoRepository;
    
    // Service per l'invio di email ai clienti
    @Autowired
    private EmailService emailService;
    
    /**
     * Salva un nuovo contatto nel database.
     * 
     * Questo metodo viene chiamato quando un cliente compila il form contatti
     * sul sito pubblico. Il contatto viene salvato con stato "non letto" (letto=false)
     * e con la data/ora di invio automatica.
     * 
     * IMPORTANTE: Invia anche notifica email all'admin per avvisarlo.
     * 
     * @param contatto L'oggetto Contatto da salvare (contiene nome, email, messaggio, ecc.)
     * @return Il contatto salvato con l'ID generato dal database
     */
    public Contatto salvaContatto(Contatto contatto) {
        Contatto saved = contattoRepository.save(contatto);
        
        // Invia notifica email all'admin (NON bloccante)
        try {
            emailService.inviaNotificaAdminNuovoContatto(saved);
        } catch (Exception e) {
            logger.warn("Impossibile inviare notifica admin per contatto ID: {}. Errore: {}", saved.getId(), e.getMessage());
        }
        
        return saved;
    }
    
    /**
     * Recupera tutti i contatti dal database, ordinati dal più recente al più vecchio.
     * 
     * Utilizzato nella pagina admin per visualizzare l'elenco completo di tutti
     * i messaggi ricevuti, indipendentemente dallo stato di lettura.
     * 
     * @return Lista di tutti i contatti ordinati per data di invio decrescente
     */
    public List<Contatto> getAllContatti() {
        return contattoRepository.findAllByOrderByDataInvioDesc();
    }
    
    /**
     * Recupera solo i contatti non ancora letti dall'amministratore.
     * 
     * Questo metodo è utile per:
     * - Mostrare notifiche all'admin di nuovi messaggi
     * - Visualizzare un contatore di messaggi da leggere
     * - Filtrare solo i messaggi che richiedono attenzione
     * 
     * @return Lista dei contatti con letto=false, ordinati dal più recente
     */
    public List<Contatto> getContattiNonLetti() {
        return contattoRepository.findByLettoOrderByDataInvioDesc(false);
    }
    
    /**
     * Recupera un singolo contatto tramite il suo ID.
     * 
     * Utilizzato quando l'admin clicca su un contatto per visualizzare
     * i dettagli completi e rispondere al cliente.
     * 
     * @param id L'identificativo univoco del contatto nel database
     * @return Optional contenente il contatto se trovato, altrimenti vuoto
     */
    public Optional<Contatto> getContattoById(Long id) {
        return contattoRepository.findById(id);
    }
    
    /**
     * Segna un contatto come "letto" dall'amministratore.
     * 
     * Questo metodo viene chiamato automaticamente quando:
     * - L'admin apre i dettagli di un contatto
     * - L'admin invia una risposta al cliente
     * 
     * Segnare come letto permette di distinguere i messaggi nuovi da quelli
     * già gestiti, migliorando l'organizzazione del lavoro.
     * 
     * @param id L'ID del contatto da segnare come letto
     * @return Il contatto aggiornato con letto=true, oppure null se non trovato
     */
    public Contatto segnaComeLetto(Long id) {
        // Cerca il contatto nel database
        Optional<Contatto> contatto = contattoRepository.findById(id);
        if (contatto.isPresent()) {
            // Se trovato, aggiorna lo stato "letto" e salva
            Contatto c = contatto.get();
            c.setLetto(true);
            return contattoRepository.save(c);
        }
        // Se non trovato, ritorna null
        return null;
    }
    
    /**
     * Invia una risposta via email al cliente che ha inviato il contatto.
     * 
     * Workflow del metodo:
     * 1. Recupera il contatto dal database tramite ID
     * 2. Invia un'email all'indirizzo del cliente con la risposta dell'admin
     * 3. Segna automaticamente il contatto come "letto"
     * 4. Registra l'esito dell'operazione nei log
     * 
     * NOTA: Se l'invio email fallisce (es. server email non disponibile),
     * l'operazione continua comunque e il contatto viene segnato come letto.
     * Questo evita che un errore temporaneo blocchi il workflow dell'admin.
     * 
     * @param id L'ID del contatto a cui rispondere
     * @param risposta Il testo della risposta scritta dall'amministratore
     */
    public void rispondiContatto(Long id, String risposta) {
        // Recupera il contatto dal database
        Optional<Contatto> contatto = contattoRepository.findById(id);
        if (contatto.isPresent()) {
            try {
                // Tenta di inviare l'email di risposta al cliente
                emailService.inviaEmailRisposta(contatto.get(), risposta);
                logger.info("Email di risposta inviata con successo al contatto ID: {}", id);
            } catch (Exception e) {
                // Se l'invio fallisce, registra un warning ma non blocca l'operazione
                // L'admin può comunque provare a contattare il cliente in altro modo
                logger.warn("Impossibile inviare email di risposta al contatto ID: {}. Errore: {}", id, e.getMessage());
            }
            // Segna il contatto come letto indipendentemente dall'esito dell'email
            segnaComeLetto(id);
        }
    }
    
    /**
     * Elimina definitivamente un contatto dal database.
     * 
     * ATTENZIONE: Questa operazione è IRREVERSIBILE!
     * Una volta eliminato, il contatto e tutte le sue informazioni
     * (messaggio, email cliente, data invio, ecc.) vengono cancellate
     * permanentemente dal database.
     * 
     * Utilizzare solo per:
     * - Spam o messaggi inappropriati
     * - Contatti duplicati
     * - Pulizia periodica di vecchi messaggi già gestiti
     * 
     * @param id L'ID del contatto da eliminare
     */
    public void eliminaContatto(Long id) {
        contattoRepository.deleteById(id);
    }
}