package com.example.demo.repository;

import com.example.demo.model.Lavoro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository per l'accesso ai dati dei Lavori (Portfolio) nel database.
 * 
 * Estende JpaRepository che fornisce automaticamente i metodi CRUD base.
 * I metodi custom usano la naming convention di Spring Data JPA per generare
 * automaticamente le query SQL.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Repository
public interface LavoroRepository extends JpaRepository<Lavoro, Long> {
    
    /**
     * Filtra i lavori per stato di pubblicazione.
     * Ordinati dal più recente al più vecchio (per data completamento).
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM lavoro WHERE pubblicato = ? ORDER BY data_fine DESC
     * 
     * Utilizzo tipico:
     * - findByPubblicatoOrderByDataFineDesc(true) → lavori visibili sul sito pubblico
     * - findByPubblicatoOrderByDataFineDesc(false) → lavori nascosti (bozze)
     * 
     * @param pubblicato true per lavori pubblicati, false per nascosti
     * @return Lista lavori filtrati per stato pubblicazione
     */
    List<Lavoro> findByPubblicatoOrderByDataFineDesc(boolean pubblicato);
    
    /**
     * Filtra i lavori per categoria.
     * Ordinati dal più recente al più vecchio.
     * 
     * Query SQL generata automaticamente:
     * SELECT * FROM lavoro WHERE categoria = ? ORDER BY data_fine DESC
     * 
     * Permette di filtrare per tipologia (Ristrutturazioni, Tetti, Pavimenti, ecc.)
     * 
     * @param categoria La categoria da filtrare
     * @return Lista lavori della categoria specificata
     */
    List<Lavoro> findByCategoriaOrderByDataFineDesc(String categoria);
}