package com.example.demo.repository;

import com.example.demo.model.Contatto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository per l'accesso ai dati dei Contatti nel database.
 * 
 * Estende JpaRepository che fornisce automaticamente i metodi CRUD base.
 * I metodi custom usano la naming convention di Spring Data JPA per generare
 * automaticamente le query SQL.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Repository
public interface ContattoRepository extends JpaRepository<Contatto, Long> {
    
    /**
     * Filtra i contatti per stato di lettura.
     * Ordinati dal più recente al più vecchio.
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM contatto WHERE letto = ? ORDER BY data_invio DESC
     * 
     * Utilizzo tipico:
     * - findByLettoOrderByDataInvioDesc(false) → contatti non letti (nuovi messaggi)
     * - findByLettoOrderByDataInvioDesc(true) → contatti già letti (archiviati)
     * 
     * @param letto true per messaggi già letti, false per nuovi messaggi
     * @return Lista contatti filtrati per stato lettura
     */
    List<Contatto> findByLettoOrderByDataInvioDesc(boolean letto);
    
    /**
     * Recupera tutti i contatti ordinati per data.
     * Dal più recente al più vecchio.
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM contatto ORDER BY data_invio DESC
     * 
     * @return Lista completa di tutti i contatti
     */
    List<Contatto> findAllByOrderByDataInvioDesc();
}