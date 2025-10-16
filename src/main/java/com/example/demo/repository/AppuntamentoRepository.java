package com.example.demo.repository;

import com.example.demo.model.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository per l'accesso ai dati degli Appuntamenti nel database.
 * 
 * Estende JpaRepository che fornisce automaticamente i metodi CRUD base:
 * - save() → salva o aggiorna
 * - findById() → trova per ID
 * - findAll() → trova tutti
 * - deleteById() → elimina per ID
 * - count() → conta record
 * 
 * I metodi custom definiti qui usano la naming convention di Spring Data JPA
 * che genera automaticamente le query SQL in base al nome del metodo.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Repository
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {
    
    /**
     * Trova tutti gli appuntamenti di un cliente tramite email.
     * Ordinati dal più recente al più vecchio.
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM appuntamento WHERE email = ? ORDER BY data_appuntamento DESC
     * 
     * @param email L'email del cliente
     * @return Lista degli appuntamenti del cliente
     */
    List<Appuntamento> findByEmailOrderByDataAppuntamentoDesc(String email);
    
    /**
     * Filtra gli appuntamenti per stato specifico.
     * Ordinati dal più vecchio al più recente (per gestire prima i più urgenti).
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM appuntamento WHERE stato = ? ORDER BY data_appuntamento ASC
     * 
     * @param stato Lo stato da filtrare (IN_ATTESA, CONFERMATO, COMPLETATO, ANNULLATO)
     * @return Lista appuntamenti con lo stato richiesto
     */
    List<Appuntamento> findByStatoOrderByDataAppuntamentoAsc(Appuntamento.StatoAppuntamento stato);
    
    /**
     * Trova appuntamenti in un intervallo di date/ore.
     * Utile per verificare disponibilità o generare report periodici.
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM appuntamento WHERE data_appuntamento BETWEEN ? AND ?
     * 
     * @param start Data/ora inizio intervallo
     * @param end Data/ora fine intervallo
     * @return Lista appuntamenti nell'intervallo specificato
     */
    List<Appuntamento> findByDataAppuntamentoBetween(LocalDateTime start, LocalDateTime end);
}