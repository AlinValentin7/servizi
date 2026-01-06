package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.model.Contatto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

/**
 * Service per l'invio di email ai clienti.
 * 
 * Questo servizio centralizza tutta la logica di invio email dell'applicazione:
 * - Email di conferma ricezione appuntamento (automatica dopo prenotazione)
 * - Email di conferma definitiva appuntamento (dall'admin)
 * - Email di rifiuto/indisponibilità appuntamento
 * - Email di risposta ai messaggi di contatto
 * 
 * CONFIGURAZIONE RICHIESTA:
 * Nel file application.properties devono essere configurati:
 * - spring.mail.host (es: smtp.gmail.com)
 * - spring.mail.port (es: 587)
 * - spring.mail.username (email mittente)
 * - spring.mail.password (password o app password)
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class EmailService {
    
    // JavaMailSender fornito da Spring Boot per invio email SMTP
    @Autowired
    private JavaMailSender mailSender;
    
    // Email mittente configurata in application.properties
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    // Formatter per date in formato italiano leggibile (es: "16/10/2025 alle ore 14:30")
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle ore' HH:mm");
    
    /**
     * Invia email automatica di conferma ricezione appuntamento al cliente.
     * 
     * Questa email viene inviata IMMEDIATAMENTE dopo che il cliente ha compilato
     * il form di prenotazione sul sito. È una conferma di "abbiamo ricevuto la tua richiesta".
     * 
     * ATTENZIONE: Questo NON è l'appuntamento definitivo confermato!
     * L'admin dovrà poi confermare o rifiutare l'appuntamento manualmente.
     * 
     * CONTENUTO EMAIL:
     * - Ringraziamento per la prenotazione
     * - Riepilogo dati appuntamento richiesto
     * - Avviso che verrà contattato per conferma definitiva
     * 
     * @param appuntamento L'appuntamento appena creato dal cliente
     */
    public void inviaEmailConfermaAppuntamento(Appuntamento appuntamento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(appuntamento.getEmail());
        message.setSubject("Conferma Appuntamento - Servizi Edili Elvis SRL");
        message.setText(
            "Gentile " + appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente() + ",\n\n" +
            "Grazie per aver prenotato un appuntamento con Servizi Edili Elvis SRL.\n\n" +
            "Dettagli dell'appuntamento:\n" +
            "Data e Ora: " + appuntamento.getDataAppuntamento() + "\n" +
            "Servizio: " + appuntamento.getTipoServizio() + "\n" +
            "Indirizzo: " + appuntamento.getIndirizzo() + "\n\n" +
            "Descrizione: " + appuntamento.getDescrizione() + "\n\n" +
            "Il nostro team ti contatterà a breve per confermare l'appuntamento.\n\n" +
            "Cordiali saluti,\n" +
            "Servizi Edili Elvis SRL"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia email di risposta a un messaggio di contatto.
     * 
     * Utilizzato quando l'admin risponde a un cliente che ha compilato
     * il form "Contatti" sul sito pubblico.
     * 
     * CONTENUTO EMAIL:
     * - Intestazione personalizzata con nome cliente
     * - Testo della risposta scritta dall'admin
     * - Firma aziendale
     * 
     * @param contatto Il contatto originale del cliente
     * @param risposta Il testo della risposta scritta dall'amministratore
     */
    public void inviaEmailRisposta(Contatto contatto, String risposta) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(contatto.getEmail());
        message.setSubject("Risposta al tuo messaggio - Servizi Edili Elvis SRL");
        message.setText(
            "Gentile " + contatto.getNome() + ",\n\n" +
            risposta + "\n\n" +
            "Cordiali saluti,\n" +
            "Servizi Edili Elvis SRL"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia email di CONFERMA DEFINITIVA appuntamento dall'admin al cliente.
     * 
     * Questa email viene inviata quando l'admin clicca "Conferma" su un appuntamento IN_ATTESA.
     * È la conferma UFFICIALE che l'appuntamento è confermato e il team si presenterà.
     * 
     * DIFFERENZA con inviaEmailConfermaAppuntamento():
     * - inviaEmailConfermaAppuntamento() = "abbiamo ricevuto la tua richiesta"
     * - inviaEmailConfermaAppuntamentoDaAdmin() = "CONFERMATO! Ci saremo sicuramente"
     * 
     * CONTENUTO EMAIL:
     * - Oggetto con emoji ✅ per attirare attenzione
     * - Conferma ufficiale dell'appuntamento
     * - Data/ora formattate in italiano (16/10/2025 alle ore 14:30)
     * - Riquadro con tutti i dettagli dell'appuntamento
     * - Contatti aziendali per necessità
     * - Promemoria durata appuntamento (1 ora)
     * 
     * @param appuntamento L'appuntamento confermato dall'admin
     */
    public void inviaEmailConfermaAppuntamentoDaAdmin(Appuntamento appuntamento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(appuntamento.getEmail());
        message.setSubject("✅ Appuntamento Confermato - Servizi Edili Elvis SRL");
        
        String dataOraFormattata = appuntamento.getDataAppuntamento().format(DATE_FORMATTER);
        
        message.setText(
            "Gentile " + appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente() + ",\n\n" +
            "Siamo lieti di confermare il tuo appuntamento con Servizi Edili Elvis SRL.\n\n" +
            "📅 DETTAGLI APPUNTAMENTO CONFERMATO:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Data e Ora: " + dataOraFormattata + "\n" +
            "Servizio richiesto: " + appuntamento.getTipoServizio() + "\n" +
            "Luogo intervento: " + (appuntamento.getIndirizzo() != null ? appuntamento.getIndirizzo() : "Da concordare") + "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "Il nostro team sarà puntuale all'orario concordato.\n" +
            "Ti ricordiamo che la durata prevista dell'appuntamento è di circa 1 ora.\n\n" +
            "Per qualsiasi necessità, non esitare a contattarci:\n" +
            "📞 Telefono: +39 320 709 7442\n" +
            "📧 Email: ristrutturazioniedili.elvis@gmail.com\n\n" +
            "Cordiali saluti,\n" +
            "Il Team di RISTRUTTURAZIONI EDILI ELVIS SRLS\n" +
            "www.serviziedilielvis.it"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia notifica email all'admin quando arriva un nuovo appuntamento.
     * 
     * Questa email avvisa l'amministratore che c'è una nuova richiesta da gestire.
     * Include tutti i dettagli per decidere rapidamente se confermare o rifiutare.
     * 
     * @param appuntamento Il nuovo appuntamento ricevuto
     */
    public void inviaNotificaAdminNuovoAppuntamento(Appuntamento appuntamento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo("ristrutturazioniedili.elvis@gmail.com"); // Email admin
        message.setSubject("🔔 NUOVO APPUNTAMENTO RICEVUTO - Azione Richiesta");
        
        String dataOraFormattata = appuntamento.getDataAppuntamento().format(DATE_FORMATTER);
        
        message.setText(
            "🔔 NUOVO APPUNTAMENTO DA GESTIRE\n\n" +
            "Un cliente ha appena richiesto un appuntamento sul sito.\n" +
            "Accedi alla dashboard admin per confermare o rifiutare.\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📋 DETTAGLI APPUNTAMENTO:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Cliente: " + appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente() + "\n" +
            "Telefono: " + appuntamento.getTelefono() + "\n" +
            "Email: " + appuntamento.getEmail() + "\n\n" +
            "Data/Ora richiesta: " + dataOraFormattata + "\n" +
            "Servizio: " + appuntamento.getTipoServizio() + "\n" +
            "Luogo: " + (appuntamento.getIndirizzo() != null ? appuntamento.getIndirizzo() : "Non specificato") + "\n\n" +
            "Descrizione lavoro:\n" +
            (appuntamento.getDescrizione() != null ? appuntamento.getDescrizione() : "Nessuna descrizione fornita") + "\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "⚡ AZIONI RAPIDE:\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "👉 Accedi alla dashboard: http://localhost:8080/admin/dashboard\n" +
            "👉 Gestisci appuntamenti: http://localhost:8080/admin/appuntamenti\n\n" +
            "⚠️ IMPORTANTE: Rispondi entro 24 ore per non perdere il cliente!\n\n" +
            "Servizi Edili Elvis SRL - Sistema Gestionale"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia notifica email all'admin quando arriva un nuovo contatto.
     * 
     * @param contatto Il nuovo messaggio di contatto ricevuto
     */
    public void inviaNotificaAdminNuovoContatto(Contatto contatto) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo("ristrutturazioniedili.elvis@gmail.com"); // Email admin
        message.setSubject("💬 NUOVO MESSAGGIO CONTATTO - " + contatto.getNome());
        
        message.setText(
            "💬 NUOVO MESSAGGIO DA LEGGERE\n\n" +
            "Un visitatore ha inviato un messaggio dal form contatti del sito.\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Da: " + contatto.getNome() + "\n" +
            "Email: " + contatto.getEmail() + "\n" +
            "Telefono: " + (contatto.getTelefono() != null ? contatto.getTelefono() : "Non fornito") + "\n\n" +
            "Messaggio:\n" +
            contatto.getMessaggio() + "\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "👉 Rispondi dalla dashboard: http://localhost:8080/admin/contatti\n\n" +
            "Servizi Edili Elvis SRL - Sistema Gestionale"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia email di RIFIUTO/INDISPONIBILITÀ per l'appuntamento richiesto.
     * 
     * Questa email viene inviata quando l'admin clicca "Rifiuta" su un appuntamento IN_ATTESA
     * e inserisce una motivazione del rifiuto.
     * 
     * SCOPO DELL'EMAIL:
     * - Informare il cliente che NON siamo disponibili nella fascia oraria richiesta
     * - Spiegare il MOTIVO del rifiuto (motivazione scritta dall'admin)
     * - Invitare il cliente a riprogrammare scegliendo un'altra data
     * - Fornire tutti i contatti per assistenza nella riprogrammazione
     * 
     * CONTENUTO EMAIL:
     * - Oggetto con emoji ⚠️ per segnalare problema
     * - Ringraziamento e scuse per l'inconveniente
     * - Riepilogo dell'appuntamento richiesto (per riferimento)
     * - MOTIVAZIONE del rifiuto (es: "Siamo già impegnati in quella fascia")
     * - Link diretto alla pagina prenotazioni per riprogrammare
     * - Contatti multipli (telefono, email, WhatsApp) per assistenza
     * 
     * TONO: Professionale ma cordiale, con scuse per l'inconveniente.
     * L'obiettivo è far riprogrammare il cliente, non perderlo.
     * 
     * @param appuntamento L'appuntamento rifiutato
     * @param motivazione La motivazione del rifiuto inserita dall'admin
     */
    public void inviaEmailRifiutoAppuntamento(Appuntamento appuntamento, String motivazione) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(appuntamento.getEmail());
        message.setSubject("⚠️ Appuntamento Non Disponibile - Servizi Edili Elvis SRL");
        
        String dataOraFormattata = appuntamento.getDataAppuntamento().format(DATE_FORMATTER);
        
        message.setText(
            "Gentile " + appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente() + ",\n\n" +
            "Ti ringraziamo per aver richiesto un appuntamento con Servizi Edili Elvis SRL.\n\n" +
            "Purtroppo dobbiamo informarti che NON siamo disponibili per la data e l'ora richieste:\n\n" +
            "📅 Data e Ora richieste: " + dataOraFormattata + "\n" +
            "Servizio: " + appuntamento.getTipoServizio() + "\n\n" +
            "MOTIVO:\n" +
            motivazione + "\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "Ti invitiamo a scegliere una nuova data e ora tramite il nostro sito:\n" +
            "🌐 www.serviziedilielvis.it/prenota\n\n" +
            "Oppure contattaci direttamente per concordare un nuovo appuntamento:\n" +
            "📞 Telefono: +39 320 709 7442\n" +
            "📧 Email: ristrutturazioniedili.elvis@gmail.com\n" +
            "💬 WhatsApp: +39 320 709 7442\n\n" +
            "Ci scusiamo per l'inconveniente e restiamo a tua disposizione.\n\n" +
            "Cordiali saluti,\n" +
            "Il Team di RISTRUTTURAZIONI EDILI ELVIS SRLS"
        );
        
        mailSender.send(message);
    }
}