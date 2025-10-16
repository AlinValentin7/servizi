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

/**
 * Service per la gestione del Portfolio Lavori.
 * 
 * Questo servizio gestisce tutti i lavori completati dall'azienda che vengono
 * mostrati nella sezione portfolio del sito pubblico.
 * 
 * FUNZIONALITÀ PRINCIPALI:
 * - Upload e gestione foto PRIMA/DOPO dei lavori
 * - Creazione e modifica schede lavoro
 * - Pubblicazione/nascondimento lavori dal sito pubblico
 * - Filtro per categoria (ristrutturazioni, tetti, pavimenti, ecc.)
 * - Eliminazione lavori dal portfolio
 * 
 * GESTIONE FILE:
 * Le foto vengono salvate nella directory "uploads/lavori/" con nome univoco (UUID)
 * per evitare conflitti di nomi file.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class LavoroService {
    
    // Repository per accesso ai dati dei lavori nel database
    @Autowired
    private LavoroRepository lavoroRepository;
    
    // Directory dove vengono salvate le foto dei lavori
    private final String UPLOAD_DIR = "uploads/lavori/";
    
    /**
     * Salva o aggiorna un lavoro nel database.
     * 
     * Se il lavoro ha un ID esistente, lo aggiorna.
     * Se non ha ID (o ID=null), crea un nuovo record.
     * 
     * @param lavoro L'oggetto Lavoro da salvare/aggiornare
     * @return Il lavoro salvato con ID generato/confermato
     */
    public Lavoro salvaLavoro(Lavoro lavoro) {
        return lavoroRepository.save(lavoro);
    }
    
    /**
     * Recupera TUTTI i lavori dal database (pubblicati e non).
     * 
     * Utilizzato nella pagina admin per visualizzare l'intero portfolio
     * inclusi i lavori nascosti dal sito pubblico.
     * 
     * @return Lista completa di tutti i lavori
     */
    public List<Lavoro> getAllLavori() {
        return lavoroRepository.findAll();
    }
    
    /**
     * Recupera solo i lavori PUBBLICATI, visibili sul sito pubblico.
     * 
     * Utilizzato nella pagina portfolio del sito per mostrare ai visitatori
     * solo i lavori che l'admin ha deciso di pubblicare.
     * 
     * Ordinamento: dal più recente al più vecchio (per data fine lavoro).
     * 
     * @return Lista dei lavori con pubblicato=true, ordinati per data decrescente
     */
    public List<Lavoro> getLavoriPubblicati() {
        return lavoroRepository.findByPubblicatoOrderByDataFineDesc(true);
    }
    
    /**
     * Filtra i lavori per categoria specifica.
     * 
     * Categorie tipiche:
     * - Ristrutturazioni
     * - Tetti e Coperture
     * - Pavimenti
     * - Impianti
     * - Pittura
     * ecc.
     * 
     * Utile per permettere ai visitatori di vedere solo i lavori
     * di una specifica tipologia.
     * 
     * @param categoria La categoria da filtrare
     * @return Lista di lavori della categoria richiesta, ordinati per data
     */
    public List<Lavoro> getLavoriPerCategoria(String categoria) {
        return lavoroRepository.findByCategoriaOrderByDataFineDesc(categoria);
    }
    
    /**
     * Recupera un singolo lavoro tramite il suo ID.
     * 
     * Utilizzato per visualizzare i dettagli completi di un lavoro specifico
     * sia nella pagina admin che nella pagina pubblica di dettaglio.
     * 
     * @param id L'ID univoco del lavoro
     * @return Optional contenente il lavoro se trovato, altrimenti vuoto
     */
    public Optional<Lavoro> getLavoroById(Long id) {
        return lavoroRepository.findById(id);
    }
    
    /**
     * Salva una foto su disco e restituisce l'URL per il database.
     * 
     * PROCESSO:
     * 1. Verifica che il file non sia vuoto
     * 2. Crea la directory uploads/lavori/ se non esiste
     * 3. Genera un nome file UNIVOCO usando UUID per evitare conflitti
     * 4. Salva il file fisicamente su disco
     * 5. Restituisce l'URL relativo per salvarlo nel database
     * 
     * ESEMPIO:
     * File caricato: "casa_rossi.jpg"
     * Salvato come: "a7b3c2d1-4e5f-6g7h-8i9j-0k1l2m3n4o5p.jpg"
     * URL restituito: "/uploads/lavori/a7b3c2d1-4e5f-6g7h-8i9j-0k1l2m3n4o5p.jpg"
     * 
     * PERCHÉ UUID?
     * - Evita conflitti se due clienti caricano file con stesso nome
     * - Previene sovrascritture accidentali
     * - Garantisce unicità anche con migliaia di foto
     * 
     * @param file Il file immagine caricato dal form
     * @return L'URL relativo della foto salvata, oppure null se file vuoto
     * @throws IOException se il salvataggio su disco fallisce
     */
    public String salvataggioFoto(MultipartFile file) throws IOException {
        // Se il file è vuoto, non fare nulla
        if (file.isEmpty()) {
            return null;
        }
        
        // STEP 1: Crea la directory se non esiste
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // STEP 2: Genera nome file univoco mantenendo l'estensione originale
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String filename = UUID.randomUUID().toString() + extension;
        
        // STEP 3: Salva il file fisicamente su disco
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // STEP 4: Restituisci l'URL relativo per salvarlo nel database
        return "/uploads/lavori/" + filename;
    }
    
    /**
     * Crea un nuovo lavoro con upload di foto PRIMA e DOPO.
     * 
     * WORKFLOW:
     * 1. Per ogni foto PRIMA caricata:
     *    - Salva il file su disco
     *    - Aggiungi l'URL alla lista fotoPrima del lavoro
     * 2. Per ogni foto DOPO caricata:
     *    - Salva il file su disco
     *    - Aggiungi l'URL alla lista fotoDopo del lavoro
     * 3. Salva il lavoro completo nel database con tutti gli URL delle foto
     * 
     * IMPORTANTE: Le foto vengono salvate PRIMA di salvare il lavoro nel database.
     * Se il salvataggio foto fallisce, il lavoro NON viene creato (integrità dati).
     * 
     * @param lavoro L'oggetto Lavoro con i dati (titolo, descrizione, ecc.)
     * @param fotoPrima Lista di file delle foto "prima del lavoro"
     * @param fotoDopo Lista di file delle foto "dopo il lavoro"
     * @return Il lavoro salvato con tutti gli URL delle foto
     * @throws IOException se il salvataggio di una o più foto fallisce
     */
    public Lavoro aggiungiLavoro(Lavoro lavoro, List<MultipartFile> fotoPrima, List<MultipartFile> fotoDopo) throws IOException {
        // STEP 1: Salva tutte le foto PRIMA
        if (fotoPrima != null) {
            for (MultipartFile foto : fotoPrima) {
                String fotoUrl = salvataggioFoto(foto);
                if (fotoUrl != null) {
                    lavoro.getFotoPrima().add(fotoUrl);
                }
            }
        }
        
        // STEP 2: Salva tutte le foto DOPO
        if (fotoDopo != null) {
            for (MultipartFile foto : fotoDopo) {
                String fotoUrl = salvataggioFoto(foto);
                if (fotoUrl != null) {
                    lavoro.getFotoDopo().add(fotoUrl);
                }
            }
        }
        
        // STEP 3: Salva il lavoro completo nel database
        return lavoroRepository.save(lavoro);
    }
    
    /**
     * Elimina definitivamente un lavoro dal portfolio.
     * 
     * ATTENZIONE: Operazione IRREVERSIBILE!
     * Il lavoro e tutte le sue informazioni vengono cancellate dal database.
     * 
     * NOTA: Questo metodo NON elimina le foto dal disco.
     * Le foto rimangono nella cartella uploads/lavori/ anche dopo l'eliminazione.
     * Considera di implementare una pulizia periodica delle foto orfane.
     * 
     * @param id L'ID del lavoro da eliminare
     */
    public void eliminaLavoro(Long id) {
        lavoroRepository.deleteById(id);
    }
}