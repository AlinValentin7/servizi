package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entità JPA che rappresenta un Appuntamento nel database.
 * 
 * TABELLA DATABASE: appuntamento
 * 
 * Un appuntamento è una prenotazione fatta da un cliente per richiedere
 * un sopralluogo o un intervento dell'azienda.
 * 
 * CICLO DI VITA:
 * 1. Cliente compila form → Stato IN_ATTESA
 * 2. Admin conferma → Stato CONFERMATO
 * 3. Lavoro completato → Stato COMPLETATO
 * Alternativa: Admin rifiuta → Stato ANNULLATO (con motivazione)
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Entity
public class Appuntamento {
    
    // ID univoco generato automaticamente dal database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nome del cliente (obbligatorio - validazione form)
    @NotBlank(message = "Il nome è obbligatorio")
    private String nomeCliente;
    
    // Cognome del cliente (obbligatorio - validazione form)
    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognomeCliente;
    
    // Email del cliente per invio notifiche (obbligatoria e validata)
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;
    
    // Telefono del cliente per contatti diretti (obbligatorio)
    @NotBlank(message = "Il telefono è obbligatorio")
    private String telefono;
    
    // Data e ora dell'appuntamento richiesto (obbligatorio)
    // IMPORTANTE: Ogni appuntamento occupa 1 ORA di tempo
    @NotNull(message = "La data dell'appuntamento è obbligatoria")
    private LocalDateTime dataAppuntamento;
    
    // Tipo di servizio richiesto (es: "Ristrutturazione", "Tetto", ecc.)
    @NotBlank(message = "Il tipo di servizio è obbligatorio")
    private String tipoServizio;
    
    // Descrizione dettagliata del lavoro richiesto (opzionale, max 1000 caratteri)
    @Column(length = 1000)
    private String descrizione;
    
    // Indirizzo dove effettuare il sopralluogo/intervento (opzionale)
    private String indirizzo;
    
    // Stato corrente dell'appuntamento (IN_ATTESA, CONFERMATO, COMPLETATO, ANNULLATO)
    // Salvato come STRING nel database per leggibilità
    @Enumerated(EnumType.STRING)
    private StatoAppuntamento stato = StatoAppuntamento.IN_ATTESA;
    
    // Data/ora di creazione dell'appuntamento (automatica)
    private LocalDateTime dataCreazione = LocalDateTime.now();
    
    // Motivazione del rifiuto se l'appuntamento è stato annullato (max 2000 caratteri)
    // Campo utilizzato solo quando stato = ANNULLATO
    @Column(length = 2000)
    private String motivazioneRifiuto;
    
    // Data/ora in cui l'appuntamento è stato annullato
    // Campo utilizzato solo quando stato = ANNULLATO
    private LocalDateTime dataAnnullamento;
    
    /**
     * Enum che rappresenta i possibili stati di un appuntamento.
     * 
     * IN_ATTESA: Appuntamento appena creato, in attesa di conferma admin
     * CONFERMATO: Admin ha confermato, appuntamento fissato
     * COMPLETATO: Appuntamento avvenuto e lavoro completato
     * ANNULLATO: Admin ha rifiutato o cliente ha cancellato
     */
    public enum StatoAppuntamento {
        IN_ATTESA, CONFERMATO, COMPLETATO, ANNULLATO
    }

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getCognomeCliente() {
        return cognomeCliente;
    }

    public void setCognomeCliente(String cognomeCliente) {
        this.cognomeCliente = cognomeCliente;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public LocalDateTime getDataAppuntamento() {
        return dataAppuntamento;
    }

    public void setDataAppuntamento(LocalDateTime dataAppuntamento) {
        this.dataAppuntamento = dataAppuntamento;
    }

    public String getTipoServizio() {
        return tipoServizio;
    }

    public void setTipoServizio(String tipoServizio) {
        this.tipoServizio = tipoServizio;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getIndirizzo() {
        return indirizzo;
    }

    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    public StatoAppuntamento getStato() {
        return stato;
    }

    public void setStato(StatoAppuntamento stato) {
        this.stato = stato;
    }

    public LocalDateTime getDataCreazione() {
        return dataCreazione;
    }

    public void setDataCreazione(LocalDateTime dataCreazione) {
        this.dataCreazione = dataCreazione;
    }
    
    public String getMotivazioneRifiuto() {
        return motivazioneRifiuto;
    }
    
    public void setMotivazioneRifiuto(String motivazioneRifiuto) {
        this.motivazioneRifiuto = motivazioneRifiuto;
    }
    
    public LocalDateTime getDataAnnullamento() {
        return dataAnnullamento;
    }
    
    public void setDataAnnullamento(LocalDateTime dataAnnullamento) {
        this.dataAnnullamento = dataAnnullamento;
    }
}