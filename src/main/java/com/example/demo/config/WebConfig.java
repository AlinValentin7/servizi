package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configurazione Web MVC per gestione risorse statiche.
 * 
 * PROBLEMA RISOLTO:
 * Le foto dei lavori vengono salvate in "src/main/resources/static/uploads/"
 * ma Spring Boot di default serve solo le risorse dentro "static/".
 * 
 * SOLUZIONE:
 * Questo config mappa l'URL "/uploads/**" alla directory fisica sul disco,
 * permettendo di accedere alle foto caricate dinamicamente dall'admin.
 * 
 * ESEMPIO:
 * - Foto salvata in: src/main/resources/static/uploads/abc123.jpg
 * - Accessibile via URL: http://localhost:8080/uploads/abc123.jpg
 * - Visualizzabile nelle pagine HTML: <img src="/uploads/abc123.jpg">
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configura il mapping per servire file dalla directory uploads.
     * 
     * MAPPING:
     * URL pattern: /uploads/**  (es: /uploads/lavori/foto.jpg)
     * → File system: file:src/main/resources/static/uploads/
     * 
     * Il "**" significa "qualsiasi sottocartella e file"
     * Il "file:" indica che è un percorso sul file system locale
     * 
     * IMPORTANTE:
     * Questo permette di accedere dinamicamente ai file caricati
     * dagli admin senza dover riavviare l'applicazione.
     * 
     * @param registry Il registry dove registrare i resource handlers
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:src/main/resources/static/uploads/");
    }
}
