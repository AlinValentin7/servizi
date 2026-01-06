# 📧 GUIDA COMPLETA: Configurazione Email Gmail per il Progetto

## 📊 RIEPILOGO AGGIORNAMENTI COMPLETATI

✅ Tutti i dati aziendali sono stati aggiornati in **23 file**:
- **Ragione Sociale**: RISTRUTTURAZIONI EDILI ELVIS SRLS
- **P.IVA**: IT17443671007
- **C.F.**: 17443671007
- **Indirizzo**: Via Giuseppe Palumbo, 12 - 00195 Roma (RM)
- **Telefono**: +39 320 709 7442
- **Email**: ristrutturazioniedili.elvis@gmail.com
- **WhatsApp**: +39 320 709 7442

---

## 🔐 CONFIGURAZIONE EMAIL GMAIL - GUIDA PASSO-PASSO

### **PASSO 1: Attiva la Verifica in 2 Passaggi**

1. Apri il browser e vai su: **https://myaccount.google.com**
2. Accedi con l'account: **ristrutturazioniedili.elvis@gmail.com**
3. Nel menu laterale sinistro, clicca su **"Sicurezza"**
4. Scorri verso il basso fino alla sezione **"Accesso a Google"**
5. Clicca su **"Verifica in due passaggi"**
6. Clicca su **"Inizia"** e segui le istruzioni:
   - Inserisci il tuo numero di telefono
   - Riceverai un codice via SMS
   - Inserisci il codice per confermare
7. ✅ La verifica in 2 passaggi è ora attiva

---

### **PASSO 2: Genera una Password per l'App**

⚠️ **IMPORTANTE**: Questo passaggio è visibile SOLO dopo aver attivato la verifica in 2 passaggi!

1. Torna su: **https://myaccount.google.com/security**
2. Scorri fino alla sezione **"Accesso a Google"**
3. Clicca su **"Password per le app"** 
   - Se non vedi questa opzione, assicurati che la verifica in 2 passaggi sia attiva
4. Ti verrà chiesto di inserire nuovamente la password Gmail
5. Nella schermata "Password per le app":
   - Nel menu a tendina **"Seleziona app"**, scegli **"Altra (nome personalizzato)"**
   - Scrivi: **Servizi Edili App**
   - Clicca su **"Genera"**

6. **📋 APPARIRÀ UNA PASSWORD DI 16 CARATTERI** (esempio: `abcd efgh ijkl mnop`)
   
   ⚠️ **ATTENZIONE**: 
   - Questa password apparirà UNA SOLA VOLTA!
   - Copiala immediatamente in un posto sicuro
   - NON chiudere la finestra finché non l'hai copiata

---

### **PASSO 3: Inserisci la Password nel Progetto**

Ora devi inserire la password generata nei file di configurazione del progetto.

#### **File da Modificare:**

1. **Per SVILUPPO** - Apri il file:
   ```
   C:\create\servizi\src\main\resources\application-dev.properties
   ```

2. Cerca questa sezione (circa alla riga 70):
   ```ini
   # EMAIL CONFIGURATION (DEVELOPMENT)
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=ristrutturazioniedili.elvis@gmail.com
   spring.mail.password=TUA_PASSWORD_APP_QUI
   ```

3. **SOSTITUISCI** `TUA_PASSWORD_APP_QUI` con la password di 16 caratteri che hai copiato:
   ```ini
   spring.mail.password=abcd efgh ijkl mnop
   ```
   ⚠️ **Rimuovi gli spazi** dalla password! Deve essere tutto attaccato:
   ```ini
   spring.mail.password=abcdefghijklmnop
   ```

4. Salva il file (Ctrl+S)

---

### **PASSO 4: (OPZIONALE) Configura per PRODUZIONE**

Se vuoi configurare anche l'ambiente di produzione:

1. Apri il file:
   ```
   C:\create\servizi\src\main\resources\application-prod.properties
   ```

2. In questo file, l'email è configurata per usare **variabili d'ambiente**:
   ```ini
   spring.mail.username=${MAIL_USERNAME}
   spring.mail.password=${MAIL_PASSWORD}
   ```

3. Prima di avviare in produzione, dovrai impostare queste variabili d'ambiente:
   ```bash
   set MAIL_USERNAME=ristrutturazioniedili.elvis@gmail.com
   set MAIL_PASSWORD=abcdefghijklmnop
   ```

---

### **PASSO 5: Testa la Configurazione**

1. Avvia l'applicazione Spring Boot:
   ```bash
   cd C:\create\servizi
   mvnw.cmd spring-boot:run
   ```

2. Vai sul browser: **http://localhost:8080**

3. Prova a:
   - Prenotare un appuntamento dalla pagina **"Prenota Appuntamento"**
   - Inviare un messaggio dalla pagina **"Contatti"**
   - Inviare una candidatura dalla pagina **"Lavora con Noi"**

4. Controlla la casella email **ristrutturazioniedili.elvis@gmail.com**:
   - Dovresti ricevere le email di notifica
   - Se non arrivano, controlla la cartella SPAM

---

## 🔧 RISOLUZIONE PROBLEMI

### ❌ Problema: "Autenticazione fallita"

**Causa**: Password errata o verifica in 2 passaggi non attiva

**Soluzione**:
1. Verifica che la verifica in 2 passaggi sia attiva
2. Rigenera una nuova password per l'app
3. Assicurati di aver copiato la password senza spazi
4. Controlla che non ci siano caratteri nascosti

---

### ❌ Problema: "Connection timeout"

**Causa**: Firewall o antivirus blocca la porta SMTP

**Soluzione**:
1. Disattiva temporaneamente l'antivirus
2. Controlla che la porta 587 non sia bloccata
3. Prova a usare la porta 465 (modifica nel file .properties):
   ```ini
   spring.mail.port=465
   spring.mail.properties.mail.smtp.ssl.enable=true
   ```

---

### ❌ Problema: "Email non arrivano"

**Causa**: Potrebbero essere nella cartella SPAM

**Soluzione**:
1. Controlla la cartella SPAM di Gmail
2. Contrassegna le email come "Non spam"
3. Aggiungi l'indirizzo mittente ai contatti

---

## 📝 CHECKLIST FINALE

Prima di considerare la configurazione completata, verifica:

- [ ] ✅ Verifica in 2 passaggi attiva su Gmail
- [ ] ✅ Password per l'app generata (16 caratteri)
- [ ] ✅ Password inserita in `application-dev.properties` (senza spazi)
- [ ] ✅ Email corretta: `ristrutturazioniedili.elvis@gmail.com`
- [ ] ✅ Applicazione avviata senza errori
- [ ] ✅ Test invio email completato con successo
- [ ] ✅ Email ricevuta nella casella di posta

---

## 🎯 PROSSIMI PASSI

Dopo aver configurato l'email:

1. **Personalizza i Template Email**:
   - I template si trovano in: `src/main/java/com/example/demo/service/EmailService.java`
   - Puoi modificare il contenuto delle email

2. **Configura le Notifiche Admin**:
   - Le notifiche vengono inviate anche all'admin
   - Verifica che l'admin riceva le email

3. **Test Completo**:
   - Prova tutti i form del sito
   - Verifica che le email arrivino correttamente
   - Controlla che i dati siano salvati nel database

---

## 📞 CONTATTI AGGIORNATI NEL SITO

Tutti i contatti nel sito sono stati aggiornati con:

📍 **Indirizzo**: Via Giuseppe Palumbo, 12 - 00195 Roma (RM)
📞 **Telefono**: +39 320 709 7442
📧 **Email**: ristrutturazioniedili.elvis@gmail.com
💼 **P.IVA**: IT17443671007
🏢 **Ragione Sociale**: RISTRUTTURAZIONI EDILI ELVIS SRLS

---

**Data aggiornamento**: 22 Ottobre 2025
**Versione**: 2.1 - Configurazione Email Gmail
