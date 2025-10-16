package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.model.Contatto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
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
}