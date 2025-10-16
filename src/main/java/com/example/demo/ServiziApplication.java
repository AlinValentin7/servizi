package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principale dell'applicazione Spring Boot.
 * 
 * @EnableScheduling - Abilita l'esecuzione di task schedulati (@Scheduled)
 * Necessario per:
 * - Backup automatici notturni
 * - Pulizia file orfani
 * - Invio reminder automatici
 * - Report periodici
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@SpringBootApplication
@EnableScheduling
public class ServiziApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServiziApplication.class, args);
	}

}
