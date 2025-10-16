package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Lavoro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String titolo;
    
    @Column(length = 2000)
    private String descrizione;
    
    private String cliente;
    
    private String luogo;
    
    private LocalDate dataInizio;
    
    private LocalDate dataFine;
    
    @ElementCollection
    @CollectionTable(name = "lavoro_foto_prima", joinColumns = @JoinColumn(name = "lavoro_id"))
    @Column(name = "foto_url")
    private List<String> fotoPrima = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "lavoro_foto_dopo", joinColumns = @JoinColumn(name = "lavoro_id"))
    @Column(name = "foto_url")
    private List<String> fotoDopo = new ArrayList<>();
    
    private String categoria; // es: Ristrutturazione, Imbiancatura, Pavimentazione, ecc.
    
    private boolean pubblicato = true;

    // Getter e Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }

    public LocalDate getDataInizio() {
        return dataInizio;
    }

    public void setDataInizio(LocalDate dataInizio) {
        this.dataInizio = dataInizio;
    }

    public LocalDate getDataFine() {
        return dataFine;
    }

    public void setDataFine(LocalDate dataFine) {
        this.dataFine = dataFine;
    }

    public List<String> getFotoPrima() {
        return fotoPrima;
    }

    public void setFotoPrima(List<String> fotoPrima) {
        this.fotoPrima = fotoPrima;
    }

    public List<String> getFotoDopo() {
        return fotoDopo;
    }

    public void setFotoDopo(List<String> fotoDopo) {
        this.fotoDopo = fotoDopo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isPubblicato() {
        return pubblicato;
    }

    public void setPubblicato(boolean pubblicato) {
        this.pubblicato = pubblicato;
    }
}