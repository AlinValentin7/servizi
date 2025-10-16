package com.example.demo.service;

import com.example.demo.model.Lavoro;
import com.example.demo.repository.LavoroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class LavoroService {
    
    @Autowired
    private LavoroRepository lavoroRepository;
    
    private final String UPLOAD_DIR = "uploads/lavori/";
    
    public Lavoro salvaLavoro(Lavoro lavoro) {
        return lavoroRepository.save(lavoro);
    }
    
    public List<Lavoro> getAllLavori() {
        return lavoroRepository.findAll();
    }
    
    public List<Lavoro> getLavoriPubblicati() {
        return lavoroRepository.findByPubblicatoOrderByDataFineDesc(true);
    }
    
    public List<Lavoro> getLavoriPerCategoria(String categoria) {
        return lavoroRepository.findByCategoriaOrderByDataFineDesc(categoria);
    }
    
    public Optional<Lavoro> getLavoroById(Long id) {
        return lavoroRepository.findById(id);
    }
    
    public String salvataggioFoto(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return null;
        }
        
        // Crea la directory se non esiste
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Genera nome file unico
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        
        // Salva il file
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        return "/uploads/lavori/" + filename;
    }
    
    public Lavoro aggiungiLavoro(Lavoro lavoro, List<MultipartFile> fotoPrima, List<MultipartFile> fotoDopo) throws IOException {
        // Salva foto prima
        if (fotoPrima != null) {
            for (MultipartFile foto : fotoPrima) {
                String fotoUrl = salvataggioFoto(foto);
                if (fotoUrl != null) {
                    lavoro.getFotoPrima().add(fotoUrl);
                }
            }
        }
        
        // Salva foto dopo
        if (fotoDopo != null) {
            for (MultipartFile foto : fotoDopo) {
                String fotoUrl = salvataggioFoto(foto);
                if (fotoUrl != null) {
                    lavoro.getFotoDopo().add(fotoUrl);
                }
            }
        }
        
        return lavoroRepository.save(lavoro);
    }
    
    public void eliminaLavoro(Long id) {
        lavoroRepository.deleteById(id);
    }
}