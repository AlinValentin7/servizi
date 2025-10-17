package com.example.demo.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Model per le candidature "Lavora con noi"
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Entity
@Table(name = "candidature")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Dati Anagrafici
    @NotBlank(message = "Il nome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il nome deve essere tra 2 e 50 caratteri")
    @Column(nullable = false, length = 50)
    private String nome;

    @NotBlank(message = "Il cognome è obbligatorio")
    @Size(min = 2, max = 50, message = "Il cognome deve essere tra 2 e 50 caratteri")
    @Column(nullable = false, length = 50)
    private String cognome;

    @NotNull(message = "La data di nascita è obbligatoria")
    @Past(message = "La data di nascita deve essere nel passato")
    @Column(nullable = false)
    private LocalDate dataNascita;

    @NotBlank(message = "Il luogo di nascita è obbligatorio")
    @Column(nullable = false, length = 100)
    private String luogoNascita;

    // Flag per indicare se il candidato NON ha codice fiscale (stranieri)
    @Column(nullable = false)
    private Boolean nonHaCodiceFiscale = false;

    @Pattern(regexp = "^[A-Z]{6}\\d{2}[A-Z]\\d{2}[A-Z]\\d{3}[A-Z]$", 
             message = "Codice fiscale non valido")
    @Column(length = 16)
    private String codiceFiscale;

    // Contatti
    @NotBlank(message = "L'email è obbligatoria")
    @Email(message = "Email non valida")
    @Column(nullable = false, length = 100)
    private String email;

    @NotBlank(message = "Il telefono è obbligatorio")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Numero di telefono non valido")
    @Column(nullable = false, length = 20)
    private String telefono;

    // Indirizzo
    @NotBlank(message = "L'indirizzo è obbligatorio")
    @Column(nullable = false, length = 200)
    private String indirizzo;

    @NotBlank(message = "La città è obbligatoria")
    @Column(nullable = false, length = 100)
    private String citta;

    @NotBlank(message = "La provincia è obbligatoria")
    @Size(min = 2, max = 2, message = "Provincia deve essere di 2 caratteri (es: RM)")
    @Column(nullable = false, length = 2)
    private String provincia;

    @NotBlank(message = "Il CAP è obbligatorio")
    @Pattern(regexp = "^\\d{5}$", message = "CAP non valido")
    @Column(nullable = false, length = 5)
    private String cap;

    // Posizione richiesta
    @NotBlank(message = "La posizione è obbligatoria")
    @Column(nullable = false, length = 100)
    private String posizioneRichiesta;

    // Esperienza
    @Column(length = 50)
    private String livelloEsperienza; // Junior, Intermedio, Senior

    @Column(columnDefinition = "TEXT")
    private String esperienzaPrecedente;

    @Column(length = 100)
    private String ultimoLavoro;

    // Qualifiche
    @Column(length = 100)
    private String titoloStudio;

    @Column(columnDefinition = "TEXT")
    private String competenzeTecniche;

    @Column(columnDefinition = "TEXT")
    private String certificazioni;

    @Column(columnDefinition = "TEXT")
    private String lingue;

    // Disponibilità
    @NotBlank(message = "La disponibilità è obbligatoria")
    @Column(nullable = false, length = 50)
    private String disponibilita; // Immediata, 1 mese, 2 mesi, etc.

    @Column(length = 50)
    private String tipoContratto; // Tempo indeterminato, Determinato, Partita IVA

    @Column(columnDefinition = "TEXT")
    private String messaggioMotivazionale;

    // CV Upload
    @Column(length = 255)
    private String cvFileName;

    @Column(length = 500)
    private String cvFilePath;

    // Consensi Privacy
    @Column(nullable = false)
    private Boolean privacyConsent = false;

    @Column(nullable = false)
    private Boolean dataProcessingConsent = false;

    // Metadata
    @Column(nullable = false, updatable = false)
    private LocalDateTime dataInvio;

    @Column(length = 50)
    private String stato; // Nuovo, In Valutazione, Contattato, Accettato, Rifiutato

    @Column(columnDefinition = "TEXT")
    private String noteAdmin;

    @Column
    private LocalDateTime dataRisposta;

    @Column(columnDefinition = "TEXT")
    private String messaggioRisposta;

    @PrePersist
    protected void onCreate() {
        dataInvio = LocalDateTime.now();
        if (stato == null) {
            stato = "Nuovo";
        }
    }

    // Metodo helper per il nome completo
    public String getNomeCompleto() {
        return nome + " " + cognome;
    }

    // Metodo helper per età
    public Integer getEta() {
        if (dataNascita != null) {
            return LocalDate.now().getYear() - dataNascita.getYear();
        }
        return null;
    }

    // Metodo helper per indirizzo completo
    public String getIndirizzoCompleto() {
        return indirizzo + ", " + cap + " " + citta + " (" + provincia + ")";
    }
}
