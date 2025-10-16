package com.example.demo.service;

import com.example.demo.model.Appuntamento;
import com.example.demo.repository.AppuntamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service per l'invio automatico di promemoria agli appuntamenti.
 * 
 * FUNZIONALITÀ:
 * - Invio email reminder 24 ore prima dell'appuntamento
 * - Verifica ogni ora se ci sono appuntamenti da ricordare
 * - Pulsante conferma presenza nell'email
 * - Istruzioni per disdire se necessario
 * 
 * LOGICA:
 * - Invia reminder solo per appuntamenti CONFERMATI
 * - Finestra temporale: tra 24h e 23h prima dell'appuntamento
 * - Evita invii duplicati (flag interno o controllo temporale)
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class ReminderService {
    
    @Autowired
    private AppuntamentoRepository appuntamentoRepository;
    
    @Autowired
    private JavaMailSender mailSender;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle ore' HH:mm");
    
    /**
     * Verifica ogni ora se ci sono appuntamenti che necessitano di reminder.
     * 
     * SCHEDULING:
     * - cron = "0 0 * * * ?" → Esegui ogni ora, al minuto 0
     * - Esempio: 08:00, 09:00, 10:00, 11:00, ecc.
     * 
     * FINESTRA TEMPORALE:
     * - Invia reminder tra 24 ore e 23 ore prima dell'appuntamento
     * - Esempio: Appuntamento 16/10 ore 14:00
     *   → Reminder inviato 15/10 tra le 14:00 e le 15:00
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void inviaReminderAutomatici() {
        System.out.println("⏰ Controllo appuntamenti per reminder: " + LocalDateTime.now());
        
        // Calcola finestra temporale: tra 24h e 23h da ora
        LocalDateTime ora = LocalDateTime.now();
        LocalDateTime tra24ore = ora.plusHours(24);
        LocalDateTime tra23ore = ora.plusHours(23);
        
        // Trova appuntamenti CONFERMATI nella finestra temporale
        List<Appuntamento> appuntamenti = appuntamentoRepository.findAll();
        
        long reminderInviati = 0;
        for (Appuntamento app : appuntamenti) {
            // Verifica se l'appuntamento è nella finestra temporale
            boolean nellaFinestra = app.getDataAppuntamento().isAfter(tra23ore) 
                                 && app.getDataAppuntamento().isBefore(tra24ore);
            
            // Invia reminder solo se CONFERMATO e nella finestra
            if (app.getStato() == Appuntamento.StatoAppuntamento.CONFERMATO && nellaFinestra) {
                try {
                    inviaReminderAppuntamento(app);
                    reminderInviati++;
                    System.out.println("📧 Reminder inviato per appuntamento ID: " + app.getId());
                } catch (Exception e) {
                    System.err.println("❌ Errore invio reminder per ID " + app.getId() + ": " + e.getMessage());
                }
            }
        }
        
        if (reminderInviati > 0) {
            System.out.println("✅ Inviati " + reminderInviati + " reminder");
        }
    }
    
    /**
     * Invia email di promemoria a un cliente 24h prima dell'appuntamento.
     * 
     * CONTENUTO EMAIL:
     * - Promemoria data/ora appuntamento
     * - Riepilogo servizio richiesto
     * - Indirizzo dove presentarsi
     * - Pulsante conferma presenza
     * - Contatti per disdire se necessario
     * 
     * @param appuntamento L'appuntamento per cui inviare reminder
     */
    private void inviaReminderAppuntamento(Appuntamento appuntamento) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("trandafiralinvalentin@gmail.com");
        message.setTo(appuntamento.getEmail());
        message.setSubject("⏰ PROMEMORIA APPUNTAMENTO DOMANI - Servizi Edili Elvis SRL");
        
        String dataOraFormattata = appuntamento.getDataAppuntamento().format(DATE_FORMATTER);
        
        message.setText(
            "Gentile " + appuntamento.getNomeCliente() + " " + appuntamento.getCognomeCliente() + ",\n\n" +
            "⏰ PROMEMORIA APPUNTAMENTO\n\n" +
            "Ti ricordiamo che il tuo appuntamento con Servizi Edili Elvis SRL è previsto per DOMANI:\n\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
            "📅 Data e Ora: " + dataOraFormattata + "\n" +
            "🔧 Servizio: " + appuntamento.getTipoServizio() + "\n" +
            "📍 Luogo: " + (appuntamento.getIndirizzo() != null ? appuntamento.getIndirizzo() : "Da confermare") + "\n" +
            "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
            "✅ CONFERMA LA TUA PRESENZA:\n" +
            "Se confermi la tua presenza, non è necessario fare nulla.\n" +
            "Il nostro team sarà puntuale all'appuntamento.\n\n" +
            "❌ DEVI DISDIRE?\n" +
            "Se non puoi più presentarti, ti preghiamo di avvisarci il prima possibile:\n" +
            "📞 Telefono: +39 3801590128\n" +
            "📧 Email: trandafiralinvalentin@gmail.com\n" +
            "💬 WhatsApp: +39 3801590128\n\n" +
            "⚠️ IMPORTANTE:\n" +
            "- Durata prevista: circa 1 ora\n" +
            "- Assicurati di essere disponibile all'orario concordato\n" +
            "- Prepara eventuali documenti o informazioni necessarie\n\n" +
            "Ti aspettiamo domani!\n\n" +
            "Cordiali saluti,\n" +
            "Il Team di Servizi Edili Elvis SRL\n" +
            "📞 +39 3801590128\n" +
            "📧 trandafiralinvalentin@gmail.com"
        );
        
        mailSender.send(message);
    }
    
    /**
     * Invia reminder manuale per un appuntamento specifico.
     * Utilizzabile dall'admin per inviare promemoria on-demand.
     * 
     * @param appuntamentoId ID dell'appuntamento
     * @return true se inviato, false se errore
     */
    public boolean inviaReminderManuale(Long appuntamentoId) {
        try {
            Appuntamento app = appuntamentoRepository.findById(appuntamentoId)
                .orElseThrow(() -> new RuntimeException("Appuntamento non trovato"));
            
            if (app.getStato() != Appuntamento.StatoAppuntamento.CONFERMATO) {
                System.out.println("⚠️ Reminder non inviato: appuntamento non confermato");
                return false;
            }
            
            inviaReminderAppuntamento(app);
            System.out.println("✅ Reminder manuale inviato per ID: " + appuntamentoId);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Errore invio reminder manuale: " + e.getMessage());
            return false;
        }
    }
}
