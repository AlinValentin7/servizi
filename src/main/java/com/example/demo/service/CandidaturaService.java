package com.example.demo.service;

import com.example.demo.model.Candidatura;
import com.example.demo.repository.CandidaturaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * Service per la gestione delle candidature
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@serviziedilielvis.it}")
    private String fromEmail;

    private static final String UPLOAD_DIR = "uploads/cv/";

    /**
     * Salva una nuova candidatura
     */
    @Transactional
    public Candidatura salvaCandidatura(Candidatura candidatura, MultipartFile cvFile) throws IOException {
        log.info("Salvataggio nuova candidatura per: {} {}", candidatura.getNome(), candidatura.getCognome());

        // Verifica se esiste già una candidatura con lo stesso codice fiscale (solo se presente)
        if (candidatura.getCodiceFiscale() != null && !candidatura.getCodiceFiscale().trim().isEmpty()) {
            if (candidaturaRepository.existsByCodiceFiscale(candidatura.getCodiceFiscale())) {
                log.warn("Candidatura già esistente per CF: {}", candidatura.getCodiceFiscale());
                throw new IllegalArgumentException("Esiste già una candidatura con questo codice fiscale");
            }
        }

        // Upload CV se presente
        if (cvFile != null && !cvFile.isEmpty()) {
            // Usa il codice fiscale se disponibile, altrimenti genera un ID univoco
            String identificativo = candidatura.getCodiceFiscale() != null ? 
                candidatura.getCodiceFiscale() : 
                candidatura.getEmail().replaceAll("[^a-zA-Z0-9]", "_");
            String cvPath = uploadCV(cvFile, identificativo);
            candidatura.setCvFileName(cvFile.getOriginalFilename());
            candidatura.setCvFilePath(cvPath);
        }

        // Salva candidatura
        Candidatura saved = candidaturaRepository.save(candidatura);

        // Invia email di conferma al candidato
        inviaEmailConferma(saved);

        // Invia notifica all'admin
        inviaNotificaAdmin(saved);

        log.info("Candidatura salvata con successo, ID: {}", saved.getId());
        return saved;
    }

    /**
     * Upload del CV
     */
    private String uploadCV(MultipartFile file, String codiceFiscale) throws IOException {
        // Crea directory se non esiste
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Genera nome file unico
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf(".")) 
            : ".pdf";
        String filename = codiceFiscale + "_" + UUID.randomUUID().toString() + extension;

        // Salva file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        log.info("CV caricato: {}", filename);
        return UPLOAD_DIR + filename;
    }

    /**
     * Trova tutte le candidature
     */
    public List<Candidatura> trovaTutte() {
        return candidaturaRepository.findAllByOrderByDataInvioDesc();
    }

    /**
     * Trova candidatura per ID
     */
    public Candidatura trovaPerID(Long id) {
        return candidaturaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Candidatura non trovata"));
    }

    /**
     * Trova per stato
     */
    public List<Candidatura> trovaPerStato(String stato) {
        return candidaturaRepository.findByStatoOrderByDataInvioDesc(stato);
    }

    /**
     * Aggiorna stato candidatura
     */
    @Transactional
    public Candidatura aggiornaStato(Long id, String nuovoStato, String note) {
        Candidatura candidatura = trovaPerID(id);
        candidatura.setStato(nuovoStato);
        if (note != null && !note.isEmpty()) {
            candidatura.setNoteAdmin(note);
        }
        log.info("Stato candidatura {} aggiornato a: {}", id, nuovoStato);
        return candidaturaRepository.save(candidatura);
    }

    /**
     * Rispondi a una candidatura
     */
    @Transactional
    public Candidatura rispondiCandidatura(Long id, String messaggio, String nuovoStato) {
        Candidatura candidatura = trovaPerID(id);
        candidatura.setMessaggioRisposta(messaggio);
        candidatura.setDataRisposta(LocalDateTime.now());
        candidatura.setStato(nuovoStato);

        // Invia email al candidato
        inviaEmailRisposta(candidatura, messaggio);

        log.info("Risposta inviata per candidatura {}", id);
        return candidaturaRepository.save(candidatura);
    }

    /**
     * Elimina candidatura
     */
    @Transactional
    public void eliminaCandidatura(Long id) {
        Candidatura candidatura = trovaPerID(id);
        
        // Elimina CV se presente
        if (candidatura.getCvFilePath() != null) {
            try {
                Path cvPath = Paths.get(candidatura.getCvFilePath());
                Files.deleteIfExists(cvPath);
                log.info("CV eliminato: {}", candidatura.getCvFilePath());
            } catch (IOException e) {
                log.error("Errore nell'eliminazione del CV", e);
            }
        }

        candidaturaRepository.deleteById(id);
        log.info("Candidatura {} eliminata", id);
    }

    /**
     * Statistiche candidature
     */
    public CandidaturaStats getStatistiche() {
        long totale = candidaturaRepository.count();
        long nuove = candidaturaRepository.countByStato("Nuovo");
        long inValutazione = candidaturaRepository.countByStato("In Valutazione");
        long contattati = candidaturaRepository.countByStato("Contattato");
        long accettati = candidaturaRepository.countByStato("Accettato");
        long rifiutati = candidaturaRepository.countByStato("Rifiutato");

        return new CandidaturaStats(totale, nuove, inValutazione, contattati, accettati, rifiutati);
    }

    /**
     * Invia email di conferma al candidato
     */
    private void inviaEmailConferma(Candidatura candidatura) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(candidatura.getEmail());
            message.setSubject("Candidatura ricevuta - Servizi Edili Elvis SRL");
            message.setText(String.format(
                "Gentile %s %s,\n\n" +
                "Abbiamo ricevuto la tua candidatura per la posizione di %s.\n\n" +
                "La tua candidatura è stata registrata con successo e verrà valutata dal nostro team.\n" +
                "Ti contatteremo al più presto per comunicarti l'esito della selezione.\n\n" +
                "Dati della candidatura:\n" +
                "- Posizione: %s\n" +
                "- Data invio: %s\n" +
                "- Email: %s\n" +
                "- Telefono: %s\n\n" +
                "Grazie per il tuo interesse!\n\n" +
                "Cordiali saluti,\n" +
                "Servizi Edili Elvis SRL\n" +
                "Tel: +39 380 159 0128\n" +
                "Email: info@serviziedilielvis.it",
                candidatura.getNome(),
                candidatura.getCognome(),
                candidatura.getPosizioneRichiesta(),
                candidatura.getPosizioneRichiesta(),
                candidatura.getDataInvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                candidatura.getEmail(),
                candidatura.getTelefono()
            ));

            mailSender.send(message);
            log.info("Email di conferma inviata a: {}", candidatura.getEmail());
        } catch (Exception e) {
            log.error("Errore nell'invio email di conferma", e);
        }
    }

    /**
     * Invia notifica all'admin
     */
    private void inviaNotificaAdmin(Candidatura candidatura) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(fromEmail);
            message.setSubject("Nuova candidatura ricevuta - " + candidatura.getPosizioneRichiesta());
            message.setText(String.format(
                "È stata ricevuta una nuova candidatura:\n\n" +
                "Candidato: %s\n" +
                "Posizione: %s\n" +
                "Email: %s\n" +
                "Telefono: %s\n" +
                "Esperienza: %s\n" +
                "Data invio: %s\n\n" +
                "Accedi all'area admin per visualizzare i dettagli completi.",
                candidatura.getNomeCompleto(),
                candidatura.getPosizioneRichiesta(),
                candidatura.getEmail(),
                candidatura.getTelefono(),
                candidatura.getLivelloEsperienza(),
                candidatura.getDataInvio().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            ));

            mailSender.send(message);
            log.info("Notifica admin inviata per candidatura: {}", candidatura.getId());
        } catch (Exception e) {
            log.error("Errore nell'invio notifica admin", e);
        }
    }

    /**
     * Invia email di risposta al candidato
     */
    private void inviaEmailRisposta(Candidatura candidatura, String messaggio) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(candidatura.getEmail());
            message.setSubject("Aggiornamento candidatura - Servizi Edili Elvis SRL");
            message.setText(String.format(
                "Gentile %s %s,\n\n" +
                "Ti scriviamo in merito alla tua candidatura per la posizione di %s.\n\n" +
                "%s\n\n" +
                "Stato candidatura: %s\n\n" +
                "Per qualsiasi informazione non esitare a contattarci.\n\n" +
                "Cordiali saluti,\n" +
                "Servizi Edili Elvis SRL\n" +
                "Tel: +39 380 159 0128\n" +
                "Email: info@serviziedilielvis.it",
                candidatura.getNome(),
                candidatura.getCognome(),
                candidatura.getPosizioneRichiesta(),
                messaggio,
                candidatura.getStato()
            ));

            mailSender.send(message);
            log.info("Email di risposta inviata a: {}", candidatura.getEmail());
        } catch (Exception e) {
            log.error("Errore nell'invio email di risposta", e);
        }
    }

    /**
     * Inner class per le statistiche
     */
    public record CandidaturaStats(
        long totale,
        long nuove,
        long inValutazione,
        long contattati,
        long accettati,
        long rifiutati
    ) {}
}
