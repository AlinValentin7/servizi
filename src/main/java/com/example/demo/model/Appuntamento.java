package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Appuntamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Il nome è obbligatorio")
    private String nomeCliente;
    
    @NotBlank(message = "Il cognome è obbligatorio")
    private String cognomeCliente;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;
    
    @NotBlank(message = "Il telefono è obbligatorio")
    private String telefono;
    
    @NotNull(message = "La data dell'appuntamento è obbligatoria")
    private LocalDateTime dataAppuntamento;
    
    @NotBlank(message = "Il tipo di servizio è obbligatorio")
    private String tipoServizio;
    
    @Column(length = 1000)
    private String descrizione;
    
    private String indirizzo;
    
    @Enumerated(EnumType.STRING)
    private StatoAppuntamento stato = StatoAppuntamento.IN_ATTESA;
    
    private LocalDateTime dataCreazione = LocalDateTime.now();
    
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
}