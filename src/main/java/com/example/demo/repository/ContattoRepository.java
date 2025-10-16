package com.example.demo.repository;

import com.example.demo.model.Contatto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContattoRepository extends JpaRepository<Contatto, Long> {
    List<Contatto> findByLettoOrderByDataInvioDesc(boolean letto);
    List<Contatto> findAllByOrderByDataInvioDesc();
}