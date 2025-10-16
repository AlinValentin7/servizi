package com.example.demo.repository;

import com.example.demo.model.Appuntamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {
    List<Appuntamento> findByEmailOrderByDataAppuntamentoDesc(String email);
    List<Appuntamento> findByStatoOrderByDataAppuntamentoAsc(Appuntamento.StatoAppuntamento stato);
    List<Appuntamento> findByDataAppuntamentoBetween(LocalDateTime start, LocalDateTime end);
}