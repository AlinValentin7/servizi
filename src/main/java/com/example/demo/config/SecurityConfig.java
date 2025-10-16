package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configurazione della sicurezza dell'applicazione con Spring Security.
 * 
 * FUNZIONI PRINCIPALI:
 * 1. Protezione area admin con autenticazione username/password
 * 2. Gestione login/logout
 * 3. Controllo accessi URL (chi può vedere cosa)
 * 4. Protezione CSRF (Cross-Site Request Forgery)
 * 
 * ARCHITETTURA SICUREZZA:
 * - Tutto il sito pubblico (/, /lavori, /contatti, /prenota) → LIBERO
 * - Area admin (/admin/**) → RICHIEDE LOGIN con ruolo ADMIN
 * - Risorse statiche (CSS, JS, immagini, uploads) → LIBERE
 * 
 * IMPORTANTE: Le credenziali admin sono salvate IN MEMORIA (non database).
 * Per un sistema production, considera di salvare gli utenti nel database.
 * 
 * @author Firmato $₿420
 * @since 2025
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configura la catena di filtri di sicurezza (chi può accedere a cosa).
     * 
     * REGOLE DI ACCESSO:
     * 1. /admin/** → Solo utenti autenticati con ruolo ADMIN
     * 2. Tutto il resto → Pubblico (nessuna autenticazione richiesta)
     * 
     * FORM LOGIN:
     * - Pagina login personalizzata: /admin/login
     * - Dopo login corretto → redirect a /admin/dashboard
     * - Se login fallisce → redirect a /admin/login?error=true
     * 
     * LOGOUT:
     * - URL logout: /admin/logout
     * - Dopo logout → redirect alla homepage /
     * 
     * CSRF (protezione attacchi):
     * - Disabilitato per h2-console (necessario per il testing)
     * - Disabilitato per alcune operazioni admin (upload file, eliminazioni)
     * 
     * HEADERS:
     * - frameOptions(sameOrigin) → permette iframe dalla stessa origine
     *   (necessario per h2-console durante sviluppo)
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CONFIGURAZIONE AUTORIZZAZIONI URL
            .authorizeHttpRequests(authorize -> authorize
                // Area admin protetta - solo per utenti con ruolo ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Sito pubblico e risorse statiche - accessibili a tutti
                .requestMatchers("/", "/lavori", "/lavori/**", "/contatti", "/prenota", 
                                "/conferma-appuntamento", "/h2-console/**",
                                "/css/**", "/js/**", "/images/**", "/uploads/**").permitAll()
                // Tutto il resto - permetti accesso (default permissivo)
                .anyRequest().permitAll()
            )
            // CONFIGURAZIONE FORM LOGIN
            .formLogin(form -> form
                .loginPage("/admin/login")              // Pagina login personalizzata
                .loginProcessingUrl("/admin/login")     // URL dove inviare credenziali
                .defaultSuccessUrl("/admin/dashboard", true)  // Redirect dopo login
                .failureUrl("/admin/login?error=true")  // Redirect se login fallisce
                .permitAll()                             // Login accessibile a tutti
            )
            // CONFIGURAZIONE LOGOUT
            .logout(logout -> logout
                .logoutUrl("/admin/logout")      // URL per fare logout
                .logoutSuccessUrl("/")           // Redirect dopo logout (homepage)
                .permitAll()                     // Logout accessibile a tutti
            )
            // PROTEZIONE CSRF (disabilita per alcune URL necessarie)
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**", "/admin/lavori/salva", 
                                        "/admin/lavori/elimina/**", 
                                        "/admin/lavori/toggle-pubblicazione/**")
            )
            // CONFIGURAZIONE HEADERS (permetti iframe per h2-console)
            .headers(headers -> headers
                .frameOptions(frame -> frame.sameOrigin())
            );
        
        return http.build();
    }

    /**
     * Crea e configura l'utente ADMIN per accedere al pannello amministrativo.
     * 
     * CREDENZIALI ADMIN:
     * Username: elvisadmin
     * Password: adminelvis12345
     * Ruolo: ADMIN
     * 
     * IMPORTANTE SICUREZZA:
     * - La password è CODIFICATA con BCrypt (hash sicuro, non reversibile)
     * - L'utente è salvato IN MEMORIA (InMemoryUserDetailsManager)
     * - Ad ogni riavvio dell'applicazione, l'utente viene ricreato
     * 
     * NOTA PER IL FUTURO:
     * Per un sistema production, considera di:
     * 1. Salvare utenti nel database (non in memoria)
     * 2. Permettere cambio password dall'interfaccia admin
     * 3. Implementare recupero password via email
     * 4. Aggiungere autenticazione a due fattori (2FA)
     * 
     * @return Service che gestisce l'utente admin in memoria
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Crea l'utente admin con credenziali e ruolo
        UserDetails admin = User.builder()
            .username("elvisadmin")                              // Username per login
            .password(passwordEncoder().encode("adminelvis12345")) // Password codificata
            .roles("ADMIN")                                      // Ruolo ADMIN
            .build();

        // Restituisci manager che tiene l'utente in memoria
        return new InMemoryUserDetailsManager(admin);
    }

    /**
     * Configura il codificatore di password BCrypt.
     * 
     * BCrypt è un algoritmo di hashing sicuro per password:
     * - NON è reversibile (hash one-way)
     * - Include automaticamente un "salt" casuale
     * - È progettato per essere lento (resistente a brute-force)
     * 
     * FUNZIONAMENTO:
     * - Al login: confronta hash(password_inserita) con hash_salvato
     * - Non salva mai la password in chiaro nel database
     * 
     * @return Encoder BCrypt per codifica sicura password
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

