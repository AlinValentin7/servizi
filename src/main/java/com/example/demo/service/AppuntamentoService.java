package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.repository.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service per la gestione degli Appuntamenti del sistema.
 * 
 * Questo servizio gestisce l'intero ciclo di vita degli appuntamenti:
 * - Creazione e prenotazione con verifica disponibilità oraria
 * - Conferma da parte dell'amministratore
 * - Rifiuto con motivazione
 * - Gestione stati (IN_ATTESA, CONFERMATO, COMPLETATO, ANNULLATO)
 * - Invio notifiche email e WhatsApp ai clienti
 * 
 * LOGICA IMPORTANTE: Ogni appuntamento occupa uno slot di 1 ORA.
 * Il sistema impedisce sovrapposizioni di appuntamenti per evitare conflitti.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class AppuntamentoService {
    
    // Repository per accesso ai dati degli appuntamenti nel database
    @Autowired
    private AppuntamentoRepository appuntamentoRepository;
    
    // Service per invio email di conferma/rifiuto ai clienti
    @Autowired
    private EmailService emailService;
    
    // Service per invio notifiche WhatsApp (opzionale)
    @Autowired
    private WhatsAppService whatsAppService;
    
    /**
     * Verifica se uno slot orario è disponibile per un nuovo appuntamento.
     * 
     * LOGICA DI FUNZIONAMENTO:
     * - Ogni appuntamento dura 1 ORA (tempo fisso)
     * - Se richiedo appuntamento alle 10:00, occuperò dalle 10:00 alle 11:00
     * - Il sistema verifica che non ci siano altri appuntamenti in questo intervallo
     * - Gli appuntamenti ANNULLATI non vengono considerati (slot liberato)
     * 
     * ALGORITMO DI VERIFICA SOVRAPPOSIZIONE:
     * Due appuntamenti si sovrappongono se:
     * - Il nuovo inizia PRIMA che finisca quello esistente E
     * - Il nuovo finisce DOPO che inizia quello esistente
     * 
     * Esempio di sovrapposizione:
     * Esistente: 10:00 - 11:00
     * Nuovo: 10:30 - 11:30 → SOVRAPPOSIZIONE (nuovo inizia prima delle 11:00 e finisce dopo le 10:00)
     * 
     * Esempio senza sovrapposizione:
     * Esistente: 10:00 - 11:00
     * Nuovo: 11:00 - 12:00 → OK (nuovo inizia esattamente quando finisce l'esistente)
     * 
     * @param dataOraRichiesta La data/ora di inizio dell'appuntamento richiesto
     * @return true se lo slot è libero, false se occupato
     */
    public boolean isSlotDisponibile(LocalDateTime dataOraRichiesta) {
        // Calcola l'intervallo temporale dello slot richiesto (1 ora)
        LocalDateTime inizioSlot = dataOraRichiesta;
        LocalDateTime fineSlot = dataOraRichiesta.plusHours(1);
        
        // Recupera tutti gli appuntamenti esistenti dal database
        List<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        
        // Controlla ogni appuntamento esistente per verificare sovrapposizioni
        for (Appuntamento app : appuntamenti) {
            // Gli appuntamenti annullati non occupano slot (ignoriamoli)
            if (app.getStato() == Appuntamento.StatoAppuntamento.ANNULLATO) {
                continue;
            }
            
            // Calcola l'intervallo dell'appuntamento esistente (1 ora)
            LocalDateTime inizioEsistente = app.getDataAppuntamento();
            LocalDateTime fineEsistente = inizioEsistente.plusHours(1);
            
            // Verifica sovrapposizione con l'algoritmo temporale:
            // C'è sovrapposizione se il nuovo inizia prima che finisca l'esistente
            // E il nuovo finisce dopo che inizia l'esistente
            boolean sovrapposizione = inizioSlot.isBefore(fineEsistente) && fineSlot.isAfter(inizioEsistente);
            
            if (sovrapposizione) {
                return false; // Slot occupato - appuntamento non disponibile
            }
        }
        
        return true; // Nessuna sovrapposizione trovata - slot libero
    }
    
    /**
     * Crea un nuovo appuntamento dopo aver verificato la disponibilità dello slot.
     * 
     * WORKFLOW:
     * 1. Verifica che lo slot orario sia disponibile (no sovrapposizioni)
     * 2. Salva l'appuntamento nel database con stato IN_ATTESA
     * 3. Invia email di conferma ricezione al cliente
     * 4. Invia notifica WhatsApp all'admin (opzionale)
     * 
     * IMPORTANTE: Se l'invio email/WhatsApp fallisce, l'appuntamento viene
     * salvato comunque. Le notifiche sono considerate "best effort" e non
     * devono bloccare la prenotazione del cliente.
     * 
     * @param appuntamento L'oggetto Appuntamento compilato dal cliente
     * @return L'appuntamento salvato con ID generato
     * @throws IllegalStateException se lo slot è già occupato
     */
    public Appuntamento creaAppuntamento(Appuntamento appuntamento) {
        // STEP 1: Verifica disponibilità dello slot prima di procedere
        if (!isSlotDisponibile(appuntamento.getDataAppuntamento())) {
            // Slot occupato - lancia eccezione che verrà gestita dal controller
            throw new IllegalStateException("FASCIA_ORARIA_NON_DISPONIBILE");
        }
        
        // STEP 2: Slot libero - salva l'appuntamento nel database
        Appuntamento saved = appuntamentoRepository.save(appuntamento);
        
        // STEP 3: Invia email di conferma ricezione al cliente (NON bloccante)
        try {
            emailService.inviaEmailConfermaAppuntamento(saved);
        } catch (Exception e) {
            // Se fallisce, stampa avviso ma continua - non blocchiamo la prenotazione
            System.out.println("Avviso: Impossibile inviare email di conferma. " + e.getMessage());
        }
        
        // STEP 4: Invia notifica WhatsApp all'admin (opzionale - NON bloccante)
        try {
            whatsAppService.inviaNotificaAppuntamento(saved);
        } catch (Exception e) {
            // Se fallisce, stampa avviso ma continua
            System.out.println("Avviso: Impossibile inviare notifica WhatsApp. " + e.getMessage());
        }
        
        return saved;
    }
    
    /**
     * Recupera tutti gli appuntamenti dal database.
     * Utilizzato nella pagina admin per visualizzare l'elenco completo.
     * 
     * @return Lista di tutti gli appuntamenti (tutti gli stati)
     */
    public List<Appuntamento> getAllAppuntamenti() {
        return appuntamentoRepository.findAll();
    }
    
    /**
     * Recupera un singolo appuntamento tramite il suo ID.
     * Utilizzato per visualizzare i dettagli di un appuntamento specifico.
     * 
     * @param id L'ID univoco dell'appuntamento
     * @return Optional contenente l'appuntamento se trovato, altrimenti vuoto
     */
    public Optional<Appuntamento> getAppuntamentoById(Long id) {
        return appuntamentoRepository.findById(id);
    }
    
    /**
     * Filtra gli appuntamenti per stato specifico.
     * Utile per visualizzare solo appuntamenti IN_ATTESA, CONFERMATI, ecc.
     * 
     * @param stato Lo stato da filtrare (IN_ATTESA, CONFERMATO, COMPLETATO, ANNULLATO)
     * @return Lista di appuntamenti con lo stato richiesto, ordinati per data
     */
    public List<Appuntamento> getAppuntamentiByStato(Appuntamento.StatoAppuntamento stato) {
        return appuntamentoRepository.findByStatoOrderByDataAppuntamentoAsc(stato);
    }
    
    /**
     * Aggiorna manualmente lo stato di un appuntamento.
     * 
     * NOTA: Preferire i metodi specifici confermaAppuntamento() e rifiutaAppuntamento()
     * che gestiscono anche l'invio delle email. Questo metodo è per cambi stato
     * semplici senza notifiche (es: CONFERMATO → COMPLETATO).
     * 
     * @param id L'ID dell'appuntamento da aggiornare
     * @param nuovoStato Il nuovo stato da impostare
     * @return L'appuntamento aggiornato, oppure null se non trovato
     */
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
     * Conferma un appuntamento e invia email di conferma al cliente.
     * 
     * WORKFLOW:
     * 1. Recupera l'appuntamento dal database tramite ID
     * 2. Cambia lo stato da IN_ATTESA a CONFERMATO
     * 3. Salva l'aggiornamento nel database
     * 4. Invia email di conferma al cliente con tutti i dettagli
     * 
     * IMPORTANTE: Se l'invio email fallisce, viene lanciata un'eccezione
     * e l'operazione viene bloccata. Questo perché la conferma DEVE essere
     * comunicata al cliente, altrimenti potrebbe non presentarsi.
     * 
     * @param id L'ID dell'appuntamento da confermare
     * @throws RuntimeException se l'invio email fallisce
     */
    public void confermaAppuntamento(Long id) {
        Optional<Appuntamento> appuntamentoOpt = appuntamentoRepository.findById(id);
        if (appuntamentoOpt.isPresent()) {
            Appuntamento app = appuntamentoOpt.get();
            // Cambia stato a CONFERMATO
            app.setStato(Appuntamento.StatoAppuntamento.CONFERMATO);
            appuntamentoRepository.save(app);
            
            try {
                // Invia email di conferma al cliente (BLOCCANTE - deve riuscire)
                emailService.inviaEmailConfermaAppuntamentoDaAdmin(app);
            } catch (Exception e) {
                System.err.println("Errore invio email conferma appuntamento: " + e.getMessage());
                // Lancia eccezione per bloccare l'operazione - la conferma è critica
                throw new RuntimeException("Impossibile inviare email di conferma");
            }
        }
    }
    
    /**
     * Rifiuta un appuntamento per indisponibilità e invia email al cliente.
     * 
     * WORKFLOW:
     * 1. Recupera l'appuntamento dal database tramite ID
     * 2. Cambia lo stato a ANNULLATO
     * 3. Salva la motivazione del rifiuto fornita dall'admin
     * 4. Registra la data/ora di annullamento
     * 5. Salva tutto nel database
     * 6. Invia email al cliente con motivazione e suggerimenti per riprogrammare
     * 
     * IMPORTANTE: La motivazione viene salvata nel database per tracciabilità.
     * Serve per:
     * - Storico delle decisioni dell'admin
     * - Visualizzazione dettagli appuntamento annullato
     * - Analisi dei motivi di rifiuto più comuni
     * 
     * Se l'invio email fallisce, l'operazione viene bloccata perché il cliente
     * DEVE essere informato del rifiuto, altrimenti potrebbe presentarsi inutilmente.
     * 
     * @param id L'ID dell'appuntamento da rifiutare
     * @param motivazione La motivazione del rifiuto scritta dall'admin
     * @throws RuntimeException se l'invio email fallisce
     */
    public void rifiutaAppuntamento(Long id, String motivazione) {
        Optional<Appuntamento> appuntamentoOpt = appuntamentoRepository.findById(id);
        if (appuntamentoOpt.isPresent()) {
            Appuntamento app = appuntamentoOpt.get();
            // Cambia stato a ANNULLATO
            app.setStato(Appuntamento.StatoAppuntamento.ANNULLATO);
            // Salva la motivazione per tracciabilità
            app.setMotivazioneRifiuto(motivazione);
            // Registra quando è stato annullato
            app.setDataAnnullamento(LocalDateTime.now());
            appuntamentoRepository.save(app);
            
            try {
                // Invia email di rifiuto al cliente con la motivazione (BLOCCANTE)
                emailService.inviaEmailRifiutoAppuntamento(app, motivazione);
            } catch (Exception e) {
                System.err.println("Errore invio email rifiuto appuntamento: " + e.getMessage());
                // Lancia eccezione - il cliente deve sapere del rifiuto
                throw new RuntimeException("Impossibile inviare email di rifiuto");
            }
        }
    }
    
    /**
     * Elimina definitivamente un appuntamento dal database.
     * 
     * ATTENZIONE: Operazione IRREVERSIBILE!
     * L'appuntamento e tutti i suoi dati (motivazione rifiuto inclusa)
     * vengono cancellati permanentemente.
     * 
     * Utilizzare solo per:
     * - Appuntamenti di test
     * - Duplicati
     * - Pulizia database vecchi appuntamenti completati
     * 
     * @param id L'ID dell'appuntamento da eliminare
     */
    public void eliminaAppuntamento(Long id) {
        appuntamentoRepository.deleteById(id);
    }
}