package com.example.demo.repository;

import com.example.demo.model.Lavoro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LavoroRepository extends JpaRepository<Lavoro, Long> {
    List<Lavoro> findByPubblicatoOrderByDataFineDesc(boolean pubblicato);
    List<Lavoro> findByCategoriaOrderByDataFineDesc(String categoria);
}