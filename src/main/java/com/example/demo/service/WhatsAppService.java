package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class WhatsAppService {
    
    @Value("${whatsapp.business.number}")
    private String businessNumber;
    
    @Value("${whatsapp.api.url}")
    private String whatsappApiUrl;
    
    public String generaLinkWhatsApp(Appuntamento appuntamento) {
        String messaggio = "Ciao! Ho prenotato un appuntamento per il servizio: " + 
                          appuntamento.getTipoServizio() + 
                          " in data " + appuntamento.getDataAppuntamento();
        
        String encodedMessage = URLEncoder.encode(messaggio, StandardCharsets.UTF_8);
        return whatsappApiUrl + "?phone=" + businessNumber + "&text=" + encodedMessage;
    }
    
    public void inviaNotificaAppuntamento(Appuntamento appuntamento) {
        // Log per debug - In produzione si può integrare con WhatsApp Business API
        System.out.println("Notifica WhatsApp per appuntamento #" + appuntamento.getId());
        System.out.println("Cliente: " + appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente());
        System.out.println("Telefono: " + appuntamento.getTelefono());
        System.out.println("Link WhatsApp: " + generaLinkWhatsApp(appuntamento));
    }
}