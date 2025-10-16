package com.example.demo.validator;

import com.example.demo.model.Appuntamento;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * Validatore per le regole di business degli appuntamenti.
 * 
 * REGOLE VALIDATE:
 * - Orario lavorativo: 8:00 - 20:00
 * - Giorni lavorativi: Lunedì - Venerdì (no weekend)
 * - Data futura: non si possono prenotare appuntamenti nel passato
 * - Anticipo minimo: almeno 2 ore di preavviso
 * - Durata appuntamento: sempre 1 ora (60 minuti)
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Component
public class AppuntamentoValidator {
    
    private static final int ORA_APERTURA = 8;
    private static final int ORA_CHIUSURA = 20;
    private static final int ORE_PREAVVISO_MINIMO = 2;
    
    /**
     * Valida tutte le regole di business per un appuntamento.
     * 
     * @param appuntamento L'appuntamento da validare
     * @throws IllegalArgumentException se una o più regole non sono rispettate
     */
    public void valida(Appuntamento appuntamento) {
        LocalDateTime dataOra = appuntamento.getDataAppuntamento();
        
        // REGOLA 1: Data/ora non può essere nel passato
        if (dataOra.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                "Non puoi prenotare un appuntamento nel passato. Seleziona una data futura."
            );
        }
        
        // REGOLA 2: Preavviso minimo 2 ore
        LocalDateTime minimoConsentito = LocalDateTime.now().plusHours(ORE_PREAVVISO_MINIMO);
        if (dataOra.isBefore(minimoConsentito)) {
            throw new IllegalArgumentException(
                "Devi prenotare con almeno " + ORE_PREAVVISO_MINIMO + " ore di anticipo. " +
                "Per urgenze, contattaci telefonicamente al +39 3801590128"
            );
        }
        
        // REGOLA 3: Solo giorni lavorativi (Lunedì-Venerdì, no weekend)
        DayOfWeek giornoSettimana = dataOra.getDayOfWeek();
        if (giornoSettimana == DayOfWeek.SATURDAY || giornoSettimana == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException(
                "Gli appuntamenti sono disponibili solo nei giorni lavorativi (Lunedì-Venerdì). " +
                "Weekend e festivi siamo chiusi."
            );
        }
        
        // REGOLA 4: Orario lavorativo 8:00 - 20:00
        int ora = dataOra.getHour();
        if (ora < ORA_APERTURA || ora >= ORA_CHIUSURA) {
            throw new IllegalArgumentException(
                "Gli appuntamenti sono disponibili solo dalle " + ORA_APERTURA + ":00 alle " + ORA_CHIUSURA + ":00. " +
                "Orario selezionato: " + ora + ":00"
            );
        }
        
        // REGOLA 5: Campi obbligatori compilati
        if (appuntamento.getNomeCliente() == null || appuntamento.getNomeCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome è obbligatorio");
        }
        
        if (appuntamento.getCognomeCliente() == null || appuntamento.getCognomeCliente().trim().isEmpty()) {
            throw new IllegalArgumentException("Il cognome è obbligatorio");
        }
        
        if (appuntamento.getEmail() == null || appuntamento.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("L'email è obbligatoria");
        }
        
        if (appuntamento.getTelefono() == null || appuntamento.getTelefono().trim().isEmpty()) {
            throw new IllegalArgumentException("Il telefono è obbligatorio");
        }
        
        if (appuntamento.getTipoServizio() == null || appuntamento.getTipoServizio().trim().isEmpty()) {
            throw new IllegalArgumentException("Il tipo di servizio è obbligatorio");
        }
        
        // REGOLA 6: Validazione formato email (basilare)
        if (!appuntamento.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Formato email non valido");
        }
        
        // REGOLA 7: Validazione telefono (almeno 10 cifre)
        String telefonoSenzaSpazi = appuntamento.getTelefono().replaceAll("[^0-9]", "");
        if (telefonoSenzaSpazi.length() < 10) {
            throw new IllegalArgumentException(
                "Numero di telefono non valido. Inserisci un numero completo (es: +39 380 1590128)"
            );
        }
    }
    
    /**
     * Verifica se una data cade in un giorno festivo italiano.
     * 
     * FESTIVITÀ ITALIANE CONSIDERATE:
     * - 1 Gennaio (Capodanno)
     * - 6 Gennaio (Epifania)
     * - 25 Aprile (Liberazione)
     * - 1 Maggio (Festa del Lavoro)
     * - 2 Giugno (Festa della Repubblica)
     * - 15 Agosto (Ferragosto)
     * - 1 Novembre (Tutti i Santi)
     * - 8 Dicembre (Immacolata)
     * - 25 Dicembre (Natale)
     * - 26 Dicembre (Santo Stefano)
     * 
     * NOTA: Pasqua e Pasquetta sono mobili, algoritmo più complesso necessario
     * 
     * @param data La data da verificare
     * @return true se è festivo, false altrimenti
     */
    public boolean isFestivo(LocalDateTime data) {
        int giorno = data.getDayOfMonth();
        int mese = data.getMonthValue();
        
        // Festività fisse
        if (mese == 1 && giorno == 1) return true;   // Capodanno
        if (mese == 1 && giorno == 6) return true;   // Epifania
        if (mese == 4 && giorno == 25) return true;  // Liberazione
        if (mese == 5 && giorno == 1) return true;   // Festa del Lavoro
        if (mese == 6 && giorno == 2) return true;   // Festa della Repubblica
        if (mese == 8 && giorno == 15) return true;  // Ferragosto
        if (mese == 11 && giorno == 1) return true;  // Tutti i Santi
        if (mese == 12 && giorno == 8) return true;  // Immacolata
        if (mese == 12 && giorno == 25) return true; // Natale
        if (mese == 12 && giorno == 26) return true; // Santo Stefano
        
        return false;
    }
    
    /**
     * Verifica validità con controllo festività.
     * Versione estesa che include anche il controllo giorni festivi.
     * 
     * @param appuntamento L'appuntamento da validare
     * @throws IllegalArgumentException se cade in un festivo
     */
    public void validaConFestivita(Appuntamento appuntamento) {
        // Prima valida le regole base
        valida(appuntamento);
        
        // Poi controlla se cade in un festivo
        if (isFestivo(appuntamento.getDataAppuntamento())) {
            throw new IllegalArgumentException(
                "La data selezionata è un giorno festivo. Siamo chiusi. " +
                "Scegli un altro giorno lavorativo."
            );
        }
    }
}
