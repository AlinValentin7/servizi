package com.example.demo.service;

import com.example.demo.model.Contatto;
import com.example.demo.repository.ContattoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class ContattoService {
    
    private static final Logger logger = LoggerFactory.getLogger(ContattoService.class);
    
    @Autowired
    private ContattoRepository contattoRepository;
    
    @Autowired
    private EmailService emailService;
    
    public Contatto salvaContatto(Contatto contatto) {
        return contattoRepository.save(contatto);
    }
    
    public List<Contatto> getAllContatti() {
        return contattoRepository.findAllByOrderByDataInvioDesc();
    }
    
    public List<Contatto> getContattiNonLetti() {
        return contattoRepository.findByLettoOrderByDataInvioDesc(false);
    }
    
    public Optional<Contatto> getContattoById(Long id) {
        return contattoRepository.findById(id);
    }
    
    public Contatto segnaComeLetto(Long id) {
        Optional<Contatto> contatto = contattoRepository.findById(id);
        if (contatto.isPresent()) {
            Contatto c = contatto.get();
            c.setLetto(true);
            return contattoRepository.save(c);
        }
        return null;
    }
    
    public void rispondiContatto(Long id, String risposta) {
        Optional<Contatto> contatto = contattoRepository.findById(id);
        if (contatto.isPresent()) {
            try {
                emailService.inviaEmailRisposta(contatto.get(), risposta);
                logger.info("Email di risposta inviata con successo al contatto ID: {}", id);
            } catch (Exception e) {
                logger.warn("Impossibile inviare email di risposta al contatto ID: {}. Errore: {}", id, e.getMessage());
            }
            segnaComeLetto(id);
        }
    }
    
    public void eliminaContatto(Long id) {
        contattoRepository.deleteById(id);
    }
}