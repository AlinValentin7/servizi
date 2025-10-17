package com.example.demo.repository;

import com.example.demo.model.Candidatura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository per le candidature
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Repository
public interface CandidaturaRepository extends JpaRepository<Candidatura, Long> {

    // Trova per stato
    List<Candidatura> findByStatoOrderByDataInvioDesc(String stato);

    // Trova per posizione richiesta
    List<Candidatura> findByPosizioneRichiestaContainingIgnoreCaseOrderByDataInvioDesc(String posizione);

    // Trova per data invio
    List<Candidatura> findByDataInvioBetweenOrderByDataInvioDesc(LocalDateTime start, LocalDateTime end);

    // Trova tutte ordinate per data (più recenti prima)
    List<Candidatura> findAllByOrderByDataInvioDesc();

    // Conta per stato
    Long countByStato(String stato);

    // Verifica se esiste già una candidatura con lo stesso codice fiscale
    boolean existsByCodiceFiscale(String codiceFiscale);

    // Trova per email
    List<Candidatura> findByEmailOrderByDataInvioDesc(String email);
}
