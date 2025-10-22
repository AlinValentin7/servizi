# Guida alla Struttura del Progetto

## 📁 Organizzazione Modulare

Il progetto segue le best practice di Spring Boot con una struttura modulare chiara.

### Struttura Package

```
src/main/java/com/example/demo/
│
├── config/                     # Configurazioni Spring
│   ├── SecurityConfig.java     # Configurazione Spring Security
│   ├── EmailConfig.java        # Configurazione Email SMTP
│   └── WebConfig.java          # Configurazione Web MVC
│
├── controller/                 # Controller MVC e REST
│   ├── AppuntamentoController.java
│   ├── LavoroController.java
│   ├── ContattoController.java
│   ├── AdminController.java
│   └── api/                    # REST API separate
│       └── AppuntamentoApiController.java
│
├── service/                    # Logica Business
│   ├── AppuntamentoService.java
│   ├── LavoroService.java
│   ├── EmailService.java
│   ├── FileStorageService.java
│   └── WhatsAppService.java
│
├── repository/                 # Accesso Dati (JPA)
│   ├── AppuntamentoRepository.java
│   ├── LavoroRepository.java
│   └── ContattoRepository.java
│
├── model/                      # Entità Domain
│   ├── Appuntamento.java
│   ├── Lavoro.java
│   ├── Candidatura.java
│   └── Contatto.java
│
├── exception/                  # Eccezioni Custom
│   ├── ResourceNotFoundException.java
│   └── FileStorageException.java
│
├── validator/                  # Validatori Custom
│   └── DataAppuntamentoValidator.java
│
└── ServiziApplication.java    # Main Application Class
```

## 🎯 Responsabilità dei Layer

### 1. **Controller Layer** (`controller/`)
- **Responsabilità**: Gestire le richieste HTTP
- **NON dovrebbe**: Contenere logica business
- **Comunica con**: Service layer
- **Esempio**:
  ```java
  @Controller
  public class AppuntamentoController {
      @Autowired
      private AppuntamentoService service;
      
      @GetMapping("/prenota")
      public String mostraPagina(Model model) {
          // Solo logica di presentazione
          return "prenota";
      }
  }
  ```

### 2. **Service Layer** (`service/`)
- **Responsabilità**: Implementare la logica business
- **NON dovrebbe**: Accedere direttamente al database
- **Comunica con**: Repository layer
- **Esempio**:
  ```java
  @Service
  public class AppuntamentoService {
      @Autowired
      private AppuntamentoRepository repository;
      
      public boolean verificaDisponibilita(LocalDateTime data) {
          // Logica business complessa
      }
  }
  ```

### 3. **Repository Layer** (`repository/`)
- **Responsabilità**: Accesso e persistenza dati
- **NON dovrebbe**: Contenere logica business
- **Esempio**:
  ```java
  public interface AppuntamentoRepository extends JpaRepository<Appuntamento, Long> {
      List<Appuntamento> findByDataOraBetween(LocalDateTime start, LocalDateTime end);
  }
  ```

### 4. **Model Layer** (`model/`)
- **Responsabilità**: Definire le entità del dominio
- **Contiene**: Annotazioni JPA e validazioni
- **Esempio**:
  ```java
  @Entity
  public class Appuntamento {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;
      
      @NotBlank
      private String nomeCliente;
  }
  ```

### 5. **Configuration Layer** (`config/`)
- **Responsabilità**: Configurazioni Spring Bean
- **Esempio**:
  ```java
  @Configuration
  public class SecurityConfig {
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) {
          // Configurazione security
      }
  }
  ```

## 🧪 Struttura Test

```
src/test/java/com/example/demo/
│
├── controller/                 # Integration Tests
│   └── AppuntamentoControllerIntegrationTest.java
│
├── service/                    # Unit Tests
│   ├── AppuntamentoServiceTest.java
│   ├── LavoroServiceTest.java
│   └── EmailServiceTest.java
│
├── model/                      # Validation Tests
│   └── AppuntamentoTest.java
│
└── ServiziApplicationTests.java
```

### Tipologie di Test

1. **Unit Tests** (`service/`)
   - Testano singole classi in isolamento
   - Usano Mockito per simulare dipendenze
   - Veloci da eseguire

2. **Integration Tests** (`controller/`)
   - Testano l'integrazione tra componenti
   - Usano MockMvc per simulare HTTP
   - Caricano il contesto Spring

3. **Model Validation Tests** (`model/`)
   - Testano le validazioni Bean Validation
   - Verificano constraint @NotNull, @Email, etc.

## 🐳 Docker

### Dockerfile
- **Multi-stage build**: Riduce dimensione immagine finale
- **Non-root user**: Maggiore sicurezza
- **Healthcheck**: Monitoraggio automatico

### docker-compose.yml
Include 3 servizi:
1. **db**: PostgreSQL database
2. **app**: Applicazione Spring Boot
3. **nginx**: Reverse proxy (opzionale)

## 📊 Diagramma Flusso Richiesta

```
Client Request
      ↓
[Controller]  ← Gestisce HTTP, validazione input
      ↓
[Service]     ← Logica business, transazioni
      ↓
[Repository]  ← Accesso database (JPA)
      ↓
[Database]    ← PostgreSQL/H2
```

## 🔄 Flusso Tipico: Creazione Appuntamento

1. **Client** invia POST a `/prenota`
2. **AppuntamentoController** riceve la richiesta
3. **Controller** valida i dati del form
4. **Controller** chiama `AppuntamentoService.creaAppuntamento()`
5. **Service** verifica disponibilità slot
6. **Service** chiama `AppuntamentoRepository.save()`
7. **Repository** salva nel database
8. **Service** chiama `EmailService.inviaConferma()`
9. **Controller** reindirizza a pagina conferma

## 🛡️ Best Practices Implementate

### Separazione delle Responsabilità (SoC)
- Ogni layer ha un compito specifico
- Facilita testing e manutenzione

### Dependency Injection
- Usa `@Autowired` per iniettare dipendenze
- Favorisce loose coupling

### Validazione
- Bean Validation a livello model
- Custom validators quando necessario

### Gestione Eccezioni
- Eccezioni custom per errori business
- GlobalExceptionHandler centralizzato

### Transaction Management
- `@Transactional` sui metodi service
- Rollback automatico in caso di errore

## 📝 Convenzioni Naming

### Classi
- **Controller**: `*Controller.java`
- **Service**: `*Service.java`
- **Repository**: `*Repository.java`
- **Model**: Nome entità (es. `Appuntamento.java`)

### Metodi
- **CRUD**: `crea*`, `trova*`, `aggiorna*`, `elimina*`
- **Query**: `findBy*`, `getBy*`, `search*`
- **Business Logic**: Verbi descrittivi (es. `verificaDisponibilita`)

### Package
- Tutto lowercase
- Nomi significativi (no abbreviazioni)

## 🚀 Prossimi Passi per Crescita

### Quando il progetto cresce, considera:

1. **DTO Pattern**: Separare entità database da API response
2. **Mapper Layer**: AutoMapper/MapStruct per conversioni
3. **Service Interface**: Interfaccia + Implementazione
4. **Aspect-Oriented Programming**: Per logging cross-cutting
5. **Caching**: Spring Cache per performance
6. **Event-Driven**: ApplicationEvent per disaccoppiamento

## 📚 Risorse Utili

- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/)
- [Spring Data JPA](https://docs.spring.io/spring-data/jpa/docs/current/reference/)
- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [JUnit 5 User Guide](https://junit.org/junit5/docs/current/user-guide/)

---

**Autore**: Firmato $₿420  
**Data**: 2025
