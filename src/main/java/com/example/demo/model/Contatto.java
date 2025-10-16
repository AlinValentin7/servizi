package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
public class Contatto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Il nome è obbligatorio")
    private String nome;
    
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    private String email;
    
    private String telefono;
    
    @NotBlank(message = "Il messaggio è obbligatorio")
    @Column(length = 2000)
    private String messaggio;
    
    private LocalDateTime dataInvio = LocalDateTime.now();
    
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