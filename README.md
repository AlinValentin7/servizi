# Servizi Edili Elvis SRL - Sistema di Gestione Appuntamenti

Sistema web completo per la gestione di appuntamenti, candidature e contatti per un'azienda di servizi edili.

## 📋 Obiettivo del Progetto

Applicazione web che permette di:
- **Prenotare appuntamenti** per servizi edili
- **Candidarsi per posizioni lavorative** con upload CV
- **Contattare l'azienda** tramite form
- **Gestione amministrativa** con dashboard protetta
- **Invio automatico email** di conferma e notifiche
- **Integrazione WhatsApp** per notifiche immediate
- **Backup automatici** del database

## 🛠️ Stack Tecnologico

### Backend
- **Java 21** - Linguaggio di programmazione
- **Spring Boot 3.5.6** - Framework principale
- **Spring Data JPA** - ORM per gestione database
- **Spring Security** - Autenticazione e autorizzazione
- **Spring Mail** - Invio email automatiche
- **Hibernate** - Implementazione JPA

### Frontend
- **Thymeleaf** - Template engine server-side
- **HTML5/CSS3** - Markup e styling
- **JavaScript** - Interattività client-side
- **Bootstrap** (se presente) - Framework CSS

### Database
- **H2 Database** - Sviluppo (embedded, persistente su disco)
- **MySQL** - Produzione (opzionale)
- **PostgreSQL** - Produzione (opzionale)

### Utilità
- **Lombok** - Riduzione boilerplate code
- **Maven** - Build automation e dependency management
- **Docker** - Containerizzazione applicazione

## 📦 Requisiti

### Software Necessario
- **Java JDK 21** o superiore
- **Maven 3.6+** (oppure usa il wrapper incluso `mvnw`)
- **Docker & Docker Compose** (opzionale, per deployment containerizzato)
- **Git** (per clonare il repository)

### Configurazione Email (Gmail)
Per abilitare l'invio email:
1. Vai su [Google Account Security](https://myaccount.google.com/security)
2. Attiva la verifica in 2 passaggi
3. Vai su "Password per le app"
4. Genera una nuova password per "Mail"
5. Usa quella password nel file `.env`

## 🚀 Come Avviare il Progetto

### Metodo 1: Avvio Rapido con Maven

```bash
# Clona il repository
git clone <url-repository>
cd servizi

# Avvia l'applicazione in modalità sviluppo (usa H2 database)
mvn spring-boot:run
```

L'applicazione sarà disponibile su: **http://localhost:8080**

### Metodo 2: Build e Run del JAR

```bash
# Compila il progetto
mvn clean package

# Esegui il JAR generato
java -jar target/servizi-0.0.1-SNAPSHOT.jar
```

### Metodo 3: Avvio con Docker Compose (Raccomandato per Produzione)

```bash
# Costruisci e avvia tutti i servizi
docker-compose up -d

# Visualizza i log
docker-compose logs -f

# Ferma i servizi
docker-compose down
```

### Metodo 4: Avvio in Produzione con MySQL

```bash
# 1. Copia il file di esempio delle variabili d'ambiente
copy .env.example .env

# 2. Modifica .env con i tuoi valori reali
# (vedi sezione "Variabili d'Ambiente" sotto)

# 3. Avvia con profilo produzione
set SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run
```

## ⚙️ Variabili d'Ambiente (.env.example)

Il file `.env.example` contiene tutte le variabili di configurazione necessarie per l'ambiente di produzione:

### Variabili Database
```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/servizi_edili
```
URL di connessione al database PostgreSQL/MySQL. Formato: `jdbc:postgresql://host:porta/nome_database`

```properties
DATABASE_USERNAME=postgres
```
Nome utente per accedere al database.

```properties
DATABASE_PASSWORD=CAMBIA_QUESTA_PASSWORD
```
Password del database. **IMPORTANTE**: Cambia sempre la password di default!

### Variabili Email (SMTP)
```properties
MAIL_HOST=smtp.gmail.com
```
Server SMTP per l'invio email. Per Gmail: `smtp.gmail.com`, per altri provider consulta la loro documentazione.

```properties
MAIL_PORT=587
```
Porta SMTP. Solitamente `587` per TLS o `465` per SSL.

```properties
MAIL_USERNAME=tua-email@gmail.com
```
Indirizzo email mittente (account da cui partono le email).

```properties
MAIL_PASSWORD=tua-app-password-gmail
```
Password specifica per l'app (NON la password normale dell'email). Vedi sezione "Configurazione Email" sopra.

### Variabili WhatsApp
```properties
WHATSAPP_NUMBER=+393801590128
```
Numero WhatsApp per notifiche e contatti rapidi (formato internazionale con +39).

### Variabili Server
```properties
PORT=8080
```
Porta su cui esporre l'applicazione. Default: 8080.

```properties
UPLOAD_PATH=/var/www/servizi-edili/uploads
```
Percorso dove salvare i file caricati (CV, documenti). Deve avere permessi di scrittura.

```properties
SPRING_PROFILES_ACTIVE=prod
```
Profilo Spring da attivare:
- `dev` = Sviluppo (usa H2, logging dettagliato)
- `prod` = Produzione (usa PostgreSQL/MySQL, logging minimale)
- `mysql` = Produzione con MySQL

## 🧪 Test Automatici

### Eseguire i Test

```bash
# Esegui tutti i test
mvn test

# Esegui i test con report di copertura
mvn clean test jacoco:report

# Esegui solo test specifici
mvn test -Dtest=AppuntamentoServiceTest
```

### Struttura Test

I test si trovano in `src/test/java/com/example/demo/` e includono:

- **Test Unitari**: Testano singole classi/metodi con mock delle dipendenze
  - `AppuntamentoServiceTest` - Test logica appuntamenti
  - `LavoroServiceTest` - Test gestione lavori
  - `EmailServiceTest` - Test invio email

- **Test di Integrazione**: Testano l'integrazione tra componenti
  - `AppuntamentoControllerIntegrationTest` - Test API REST

### Tecnologie Test
- **JUnit 5** - Framework di test
- **Mockito** - Mocking delle dipendenze
- **Spring Boot Test** - Test di integrazione Spring
- **AssertJ** - Asserzioni fluide

## 🗂️ Struttura del Progetto

```
servizi/
├── src/
│   ├── main/
│   │   ├── java/com/example/demo/
│   │   │   ├── config/          # Configurazioni Spring (Security, Email, etc.)
│   │   │   ├── controller/      # Controller REST e MVC
│   │   │   │   └── api/         # API REST separate
│   │   │   ├── service/         # Logica business
│   │   │   ├── repository/      # Repository JPA (accesso database)
│   │   │   ├── model/           # Entità JPA (domain model)
│   │   │   ├── exception/       # Eccezioni personalizzate
│   │   │   ├── validator/       # Validatori custom
│   │   │   └── ServiziApplication.java  # Main class
│   │   └── resources/
│   │       ├── application.properties          # Config base
│   │       ├── application-dev.properties      # Config sviluppo
│   │       ├── application-prod.properties     # Config produzione
│   │       ├── static/          # File statici (CSS, JS, immagini)
│   │       └── templates/       # Template Thymeleaf (HTML)
│   └── test/
│       └── java/com/example/demo/  # Test unitari e integrazione
├── data/                   # Database H2 persistente (sviluppo)
├── logs/                   # File di log applicazione
├── uploads/               # File caricati dagli utenti (CV, etc.)
├── docker-compose.yml     # Orchestrazione Docker
├── Dockerfile            # Immagine Docker applicazione
├── .env.example          # Template variabili d'ambiente
├── pom.xml              # Configurazione Maven
└── README.md            # Questo file
```

## 🔐 Sicurezza

- **Spring Security** attivo con autenticazione HTTP Basic
- Password criptate con BCrypt
- CSRF protection abilitata
- Validazione input su tutti i form
- Upload file controllato (dimensione, tipo)
- Variabili sensibili in `.env` (escluso da Git)

### Accesso Admin
L'area amministrativa è protetta. Le credenziali vanno configurate in `application.properties`.

## 📧 Funzionalità Email

Il sistema invia automaticamente email per:
- ✅ Conferma appuntamento (utente + amministratore)
- ✅ Conferma candidatura ricevuta
- ✅ Nuova richiesta di contatto
- ✅ Reminder appuntamenti (schedulato)

## 🔄 Backup Automatici

Il sistema esegue backup automatici del database:
- **Frequenza**: Ogni notte alle 3:00 AM
- **Posizione**: Directory `backups/`
- **Formato**: SQL dump + compressione

## 📊 Monitoraggio

Spring Boot Actuator fornisce endpoint di monitoraggio:
- `/actuator/health` - Stato dell'applicazione
- `/actuator/info` - Informazioni versione
- `/actuator/metrics` - Metriche applicazione

## 🐳 Docker

### Build Immagine Docker

```bash
docker build -t servizi-edili:latest .
```

### Run Container Singolo

```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DATABASE_URL=jdbc:postgresql://host:5432/db \
  servizi-edili:latest
```

### Docker Compose (Stack Completo)

Il file `docker-compose.yml` include:
- **app**: Applicazione Spring Boot
- **db**: Database PostgreSQL
- **nginx**: Reverse proxy (opzionale)

## 📝 Database Console

In modalità sviluppo, puoi accedere alla console H2:
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/servizi`
- Username: `sa`
- Password: (vuota)

## 🤝 Contribuire

1. Fork del progetto
2. Crea un branch per la feature (`git checkout -b feature/NuovaFunzionalita`)
3. Commit delle modifiche (`git commit -m 'Aggiunta NuovaFunzionalita'`)
4. Push al branch (`git push origin feature/NuovaFunzionalita`)
5. Apri una Pull Request

## 📄 Licenza

Progetto proprietario - Servizi Edili Elvis SRL

## 👨‍💻 Autore

Firmato $₿420 - 2025

## 🆘 Troubleshooting

### Porta 8080 già in uso
```bash
# Cambia porta in application.properties
server.port=8081
```

### Errori di connessione database
- Verifica che il database sia avviato
- Controlla credenziali in `.env`
- Verifica URL connessione

### Email non vengono inviate
- Verifica di aver generato App Password Gmail (non la password normale)
- Controlla firewall/antivirus non blocchi porta 587
- Verifica configurazione SMTP in `.env`

### Upload file non funziona
- Verifica permessi directory `uploads/`
- Controlla `UPLOAD_PATH` in `.env`
- Verifica dimensione massima in `application.properties`

---

Per ulteriore supporto, consulta la documentazione nella cartella `archive/unused/docs/`.
