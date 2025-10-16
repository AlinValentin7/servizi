package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.model.Contatto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle ore' HH:mm");
    
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
     * Invia email di conferma appuntamento dall'admin al cliente
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
            "📞 Telefono: +39 3801590128\n" +
            "📧 Email: trandafiralinvalentin@gmail.com\n\n" +
            "Cordiali saluti,\n" +
            "Il Team di Servizi Edili Elvis SRL\n" +
            "www.serviziedilielvis.it"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia email di rifiuto/indisponibilità per l'appuntamento richiesto
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
            "📞 Telefono: +39 3801590128\n" +
            "📧 Email: trandafiralinvalentin@gmail.com\n" +
            "💬 WhatsApp: +39 3801590128\n\n" +
            "Ci scusiamo per l'inconveniente e restiamo a tua disposizione.\n\n" +
            "Cordiali saluti,\n" +
            "Il Team di Servizi Edili Elvis SRL"
        );
        
        mailSender.send(message);
    }
}