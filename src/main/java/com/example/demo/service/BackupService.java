package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Service per backup automatico del database e file.
 * 
 * FUNZIONALITÀ:
 * - Backup automatico ogni notte alle 3:00
 * - Backup manuale on-demand dall'admin
 * - Compressione ZIP per risparmiare spazio
 * - Conserva ultimi 30 backup, elimina i vecchi
 * - Backup separati per database e foto
 * 
 * STRUTTURA BACKUP:
 * backups/
 *   ├── 2025-10-16_03-00_database.zip
 *   ├── 2025-10-16_03-00_uploads.zip
 *   ├── 2025-10-17_03-00_database.zip
 *   └── ...
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Service
public class BackupService {
    
    private final String BACKUP_DIR = "backups/";
    private final String DATABASE_PATH = "data/servizi.mv.db"; // H2 database file
    private final String UPLOADS_PATH = "src/main/resources/static/uploads/";
    private final int MAX_BACKUPS_TO_KEEP = 30;
    
    /**
     * Esegue backup automatico ogni notte alle 3:00.
     * 
     * SCHEDULING:
     * - cron = "0 0 3 * * ?" → Esegui alle 3:00 ogni giorno
     * - Formato: secondi minuti ore giorno mese giorno_settimana
     * 
     * PERCHÉ ALLE 3:00?
     * - Orario con minimo traffico sul sito
     * - Evita rallentamenti durante il giorno
     * - Database in stato consistente
     */
    @Scheduled(cron = "0 0 3 * * ?")
    public void backupAutomatico() {
        try {
            System.out.println("⏰ Inizio backup automatico: " + LocalDateTime.now());
            
            // Esegui backup database e upload
            backupDatabase();
            backupUploads();
            
            // Pulisci backup vecchi (oltre 30 giorni)
            pulisciBackupVecchi();
            
            System.out.println("✅ Backup automatico completato!");
        } catch (Exception e) {
            System.err.println("❌ Errore backup automatico: " + e.getMessage());
            // TODO: Inviare email all'admin per notificare errore
        }
    }
    
    /**
     * Esegue backup manuale on-demand.
     * Chiamato dall'admin tramite pulsante "Backup Ora".
     * 
     * @return true se backup riuscito, false se errore
     */
    public boolean backupManuale() {
        try {
            System.out.println("🔧 Backup manuale avviato dall'admin");
            backupDatabase();
            backupUploads();
            return true;
        } catch (Exception e) {
            System.err.println("❌ Errore backup manuale: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Backup del file database H2 compresso in ZIP.
     */
    private void backupDatabase() throws IOException {
        Path dbPath = Paths.get(DATABASE_PATH);
        if (!Files.exists(dbPath)) {
            System.out.println("⚠️ Database non trovato, skip backup");
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String zipFilename = BACKUP_DIR + timestamp + "_database.zip";
        
        // Crea directory backup se non esiste
        Files.createDirectories(Paths.get(BACKUP_DIR));
        
        // Comprimi database in ZIP
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilename))) {
            ZipEntry entry = new ZipEntry("servizi.mv.db");
            zos.putNextEntry(entry);
            Files.copy(dbPath, zos);
            zos.closeEntry();
        }
        
        System.out.println("💾 Database backup: " + zipFilename);
    }
    
    /**
     * Backup della cartella uploads (foto lavori) compressa in ZIP.
     */
    private void backupUploads() throws IOException {
        Path uploadsPath = Paths.get(UPLOADS_PATH);
        if (!Files.exists(uploadsPath)) {
            System.out.println("⚠️ Cartella uploads non trovata, skip backup");
            return;
        }
        
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"));
        String zipFilename = BACKUP_DIR + timestamp + "_uploads.zip";
        
        // Crea directory backup se non esiste
        Files.createDirectories(Paths.get(BACKUP_DIR));
        
        // Comprimi cartella uploads in ZIP
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFilename))) {
            Files.walk(uploadsPath)
                .filter(path -> !Files.isDirectory(path))
                .forEach(path -> {
                    try {
                        String zipPath = uploadsPath.relativize(path).toString();
                        ZipEntry entry = new ZipEntry(zipPath);
                        zos.putNextEntry(entry);
                        Files.copy(path, zos);
                        zos.closeEntry();
                    } catch (IOException e) {
                        System.err.println("Errore backup file: " + path);
                    }
                });
        }
        
        System.out.println("📁 Uploads backup: " + zipFilename);
    }
    
    /**
     * Elimina backup più vecchi di 30 giorni per risparmiare spazio.
     */
    private void pulisciBackupVecchi() throws IOException {
        Path backupPath = Paths.get(BACKUP_DIR);
        if (!Files.exists(backupPath)) return;
        
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(MAX_BACKUPS_TO_KEEP);
        
        Files.list(backupPath)
            .filter(path -> {
                try {
                    return Files.getLastModifiedTime(path)
                        .toInstant()
                        .isBefore(cutoffDate.toInstant(java.time.ZoneOffset.UTC));
                } catch (IOException e) {
                    return false;
                }
            })
            .forEach(path -> {
                try {
                    Files.delete(path);
                    System.out.println("🗑️ Eliminato backup vecchio: " + path.getFileName());
                } catch (IOException e) {
                    System.err.println("Errore eliminazione backup: " + path);
                }
            });
    }
}
