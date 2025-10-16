package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Entità JPA che rappresenta un Messaggio di Contatto nel database.
 * 
 * TABELLA DATABASE: contatto
 * 
 * Un contatto è un messaggio inviato da un visitatore del sito attraverso
 * il form "Contatti". Utilizzato per richieste di informazioni, preventivi,
 * domande generali, ecc.
 * 
 * DIFFERENZA CON APPUNTAMENTO:
 * - Contatto = richiesta generica di informazioni (no data/ora)
 * - Appuntamento = prenotazione specifica con data/ora
 * 
 * WORKFLOW:
 * 1. Cliente invia messaggio → letto=false
 * 2. Admin visualizza messaggio → letto=true
 * 3. Admin risponde via email (opzionale)
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Entity
public class Contatto {
    
    // ID univoco generato automaticamente dal database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Nome del mittente (obbligatorio)
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;
    
    // Email del mittente per invio risposta (obbligatoria e validata)
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;
    
    // Telefono del mittente (opzionale)
    private String telefono;
    
    // Testo del messaggio inviato (obbligatorio, max 2000 caratteri)
    @NotBlank(message = "Il messaggio è obbligatorio")
    @Column(length = 2000)
    private String messaggio;
    
    // Data e ora di invio del messaggio (automatica)
    private LocalDateTime dataInvio = LocalDateTime.now();
    
    // Flag per indicare se l'admin ha già letto il messaggio
    // false = nuovo messaggio non letto, true = già visualizzato
    private boolean letto = false;

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    public String getMessaggio() {
        return messaggio;
    }

    public void setMessaggio(String messaggio) {
        this.messaggio = messaggio;
    }

    public LocalDateTime getDataInvio() {
        return dataInvio;
    }

    public void setDataInvio(LocalDateTime dataInvio) {
        this.dataInvio = dataInvio;
    }

    public boolean isLetto() {
        return letto;
    }

    public void setLetto(boolean letto) {
        this.letto = letto;
    }
}