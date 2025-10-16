package com.example.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entità JPA che rappresenta un Lavoro completato nel Portfolio.
 * 
 * TABELLE DATABASE:
 * - lavoro (dati principali)
 * - lavoro_foto_prima (lista URL foto prima del lavoro)
 * - lavoro_foto_dopo (lista URL foto dopo il lavoro)
 * 
 * Un lavoro è un progetto completato dall'azienda che viene mostrato
 * nella sezione portfolio del sito per dimostrare competenze ed esperienza.
 * 
 * FUNZIONE MARKETING:
 * Le foto PRIMA/DOPO sono fondamentali per:
 * - Mostrare la qualità del lavoro
 * - Conquistare la fiducia dei potenziali clienti
 * - Dimostrare esperienza in diverse tipologie di intervento
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Entity
public class Lavoro {
    
    // ID univoco generato automaticamente dal database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Titolo breve del lavoro (es: "Ristrutturazione Villa Rossi")
    private String titolo;
    
    // Descrizione dettagliata del lavoro svolto (max 2000 caratteri)
    @Column(length = 2000)
    private String descrizione;
    
    // Nome del cliente (può essere anonimizzato es: "Famiglia R.")
    private String cliente;
    
    // Località dove è stato svolto il lavoro (es: "Roma", "Milano Centro")
    private String luogo;
    
    // Data di inizio del lavoro
    private LocalDate dataInizio;
    
    // Data di completamento del lavoro
    private LocalDate dataFine;
    
    /**
     * Lista degli URL delle foto PRIMA del lavoro.
     * 
     * Salvate in tabella separata "lavoro_foto_prima" per permettere
     * un numero variabile di foto per ogni lavoro (relazione 1-N).
     * 
     * Ogni URL punta a: /uploads/lavori/[uuid].jpg
     */
    @ElementCollection
    @CollectionTable(name = "lavoro_foto_prima", joinColumns = @JoinColumn(name = "lavoro_id"))
    @Column(name = "foto_url")
    private List<String> fotoPrima = new ArrayList<>();
    
    /**
     * Lista degli URL delle foto DOPO il lavoro.
     * 
     * Salvate in tabella separata "lavoro_foto_dopo" per permettere
     * un numero variabile di foto per ogni lavoro (relazione 1-N).
     * 
     * Ogni URL punta a: /uploads/lavori/[uuid].jpg
     */
    @ElementCollection
    @CollectionTable(name = "lavoro_foto_dopo", joinColumns = @JoinColumn(name = "lavoro_id"))
    @Column(name = "foto_url")
    private List<String> fotoDopo = new ArrayList<>();
    
    /**
     * Categoria del lavoro per filtri.
     * 
     * Esempi di categorie:
     * - Ristrutturazione
     * - Imbiancatura
     * - Pavimentazione
     * - Tetti e Coperture
     * - Impianti Elettrici
     * - Bagni e Cucine
     */
    private String categoria;
    
    /**
     * Flag di pubblicazione sul sito pubblico.
     * 
     * true = visibile nel portfolio sul sito
     * false = nascosto (solo admin può vederlo)
     * 
     * Utile per:
     * - Tenere lavori in bozza prima di pubblicarli
     * - Nascondere temporaneamente lavori vecchi
     * - Gestire portfolio in base alla stagione
     */
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