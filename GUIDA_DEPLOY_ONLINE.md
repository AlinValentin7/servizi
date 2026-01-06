# 🚀 GUIDA COMPLETA PER METTERE IL SITO ONLINE

## 📋 Indice
1. [Opzioni di Deploy](#opzioni-di-deploy)
2. [Deploy Locale (per test con accesso da rete locale)](#deploy-locale)
3. [Deploy con ngrok (pubblico temporaneo)](#deploy-con-ngrok)
4. [Deploy su Server VPS (produzione)](#deploy-su-vps)
5. [Deploy su Render.com (gratis)](#deploy-su-render)
6. [Deploy su Railway (gratis)](#deploy-su-railway)

---

## 🎯 Opzioni di Deploy

### Opzione 1: LOCALE - Accesso dalla tua rete
- **Costo**: Gratis
- **Tempo setup**: 5 minuti
- **Accessibilità**: Solo dalla tua rete WiFi/LAN
- **Ideale per**: Test, demo locale

### Opzione 2: NGROK - Pubblico temporaneo
- **Costo**: Gratis (con limitazioni)
- **Tempo setup**: 10 minuti
- **Accessibilità**: Chiunque su internet (URL temporaneo)
- **Ideale per**: Demo, presentazioni, test

### Opzione 3: RENDER.COM - Hosting gratuito
- **Costo**: Gratis (con limitazioni: sleep dopo 15min di inattività)
- **Tempo setup**: 20 minuti
- **Accessibilità**: Pubblico permanente
- **Ideale per**: MVP, portfolio

### Opzione 4: RAILWAY.APP - Hosting gratuito
- **Costo**: Gratis ($5 credito mensile)
- **Tempo setup**: 15 minuti
- **Accessibilità**: Pubblico permanente
- **Ideale per**: Produzione piccola scala

### Opzione 5: VPS (Digital Ocean, Hetzner, ecc.)
- **Costo**: €5-10/mese
- **Tempo setup**: 1-2 ore
- **Accessibilità**: Pubblico permanente professionale
- **Ideale per**: Produzione seria

---

## 🏠 Deploy Locale (Accesso da rete locale)

### Passo 1: Configura l'ambiente
```cmd
copy .env.example .env
```

Modifica `.env` con i tuoi dati reali.

### Passo 2: Avvia con Docker
```cmd
docker-compose up -d
```

### Passo 3: Trova il tuo IP locale
```cmd
ipconfig
```
Cerca "IPv4 Address" (es: 192.168.1.100)

### Passo 4: Accedi
- Dal tuo PC: http://localhost:8080
- Da altri dispositivi nella tua rete: http://192.168.1.100:8080

### Passo 5: Apri la porta nel firewall Windows
```cmd
netsh advfirewall firewall add rule name="Servizi Edili" dir=in action=allow protocol=TCP localport=8080
```

✅ **Fatto!** Ora chiunque nella tua rete WiFi può accedere al sito.

---

## 🌐 Deploy con ngrok (Pubblico temporaneo - CONSIGLIATO PER INIZIARE)

### Passo 1: Scarica ngrok
Vai su https://ngrok.com e scarica ngrok per Windows

### Passo 2: Registrati (gratis)
Crea un account gratuito su https://dashboard.ngrok.com/signup

### Passo 3: Autentica ngrok
```cmd
ngrok config add-authtoken TUO_AUTH_TOKEN
```

### Passo 4: Avvia l'applicazione
```cmd
docker-compose up -d
```

### Passo 5: Esponi pubblicamente con ngrok
```cmd
ngrok http 8080
```

### Passo 6: Ottieni l'URL pubblico
ngrok ti darà un URL tipo: `https://abc123.ngrok.io`

✅ **Fatto!** Condividi questo URL con chiunque nel mondo!

**Nota**: L'URL cambia ogni volta che riavvii ngrok (nella versione gratuita).

---

## ☁️ Deploy su Render.com (Gratis)

### Passo 1: Crea account su Render.com
https://render.com/

### Passo 2: Crea un database PostgreSQL
1. Dashboard → New → PostgreSQL
2. Nome: `servizi-db`
3. Piano: Free
4. Crea e copia l'**Internal Database URL**

### Passo 3: Crea Web Service
1. Dashboard → New → Web Service
2. Collega il repository GitHub (devi prima pushare su GitHub)
3. Configurazione:
   - **Name**: servizi-edili
   - **Environment**: Docker
   - **Plan**: Free

### Passo 4: Configura variabili ambiente
Aggiungi in "Environment Variables":
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<url-postgres-interno>
DATABASE_USERNAME=<user-da-render>
DATABASE_PASSWORD=<password-da-render>
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tuaemail@gmail.com
MAIL_PASSWORD=tua-app-password
WHATSAPP_NUMBER=+393801590128
```

### Passo 5: Deploy!
Render farà automaticamente il build e deploy.

✅ **Fatto!** Il sito sarà accessibile su `https://servizi-edili.onrender.com`

**Limitazioni Free**:
- Il servizio va in sleep dopo 15 minuti di inattività
- Primo avvio lento (30-60 secondi)

---

## 🚂 Deploy su Railway.app (Gratis con credito)

### Passo 1: Crea account
https://railway.app/

### Passo 2: Crea nuovo progetto
1. New Project → Deploy from GitHub repo
2. Collega il repository

### Passo 3: Aggiungi PostgreSQL
1. Nel progetto: New → Database → PostgreSQL
2. Railway crea automaticamente le variabili

### Passo 4: Configura variabili ambiente
```
SPRING_PROFILES_ACTIVE=prod
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tuaemail@gmail.com
MAIL_PASSWORD=tua-app-password
WHATSAPP_NUMBER=+393801590128
```

### Passo 5: Deploy
Railway farà automaticamente il deploy.

✅ **Fatto!** Riceverai un URL pubblico tipo `https://servizi-edili-production.up.railway.app`

**Vantaggi**:
- $5 credito gratuito al mese
- Sempre attivo (non va in sleep)
- Più veloce di Render

---

## 🖥️ Deploy su VPS (Produzione professionale)

### Provider consigliati:
- **Hetzner Cloud**: €4/mese (Germania) - CONSIGLIATO
- **Digital Ocean**: $6/mese (USA)
- **Contabo**: €5/mese (Germania)

### Setup completo VPS

#### Passo 1: Crea VPS
- OS: Ubuntu 22.04 LTS
- RAM: Minimo 2GB
- Storage: 20GB+

#### Passo 2: Connettiti via SSH
```cmd
ssh root@IP_DEL_TUO_SERVER
```

#### Passo 3: Installa Docker
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sh get-docker.sh
apt install docker-compose -y
```

#### Passo 4: Configura firewall
```bash
ufw allow 22/tcp
ufw allow 80/tcp
ufw allow 443/tcp
ufw enable
```

#### Passo 5: Trasferisci il progetto
Sul tuo PC Windows:
```cmd
scp -r C:\create\servizi root@IP_SERVER:/root/
```

#### Passo 6: Sul server, configura ambiente
```bash
cd /root/servizi
nano .env
```
Inserisci le tue configurazioni.

#### Passo 7: Avvia l'applicazione
```bash
docker-compose up -d
```

#### Passo 8: Configura dominio (opzionale)
1. Compra dominio su Namecheap/GoDaddy
2. Punta il dominio all'IP del server
3. Installa Certbot per SSL:
```bash
apt install certbot python3-certbot-nginx -y
certbot --nginx -d tuodominio.com
```

✅ **Fatto!** Il sito è online professionale su `https://tuodominio.com`

---

## 📧 Configurazione Email Gmail

### Passo 1: Attiva 2FA su Gmail
1. Vai su https://myaccount.google.com/security
2. Attiva "Verifica in due passaggi"

### Passo 2: Crea App Password
1. Vai su https://myaccount.google.com/apppasswords
2. Seleziona "Mail" e "Altri"
3. Nome: "Servizi Edili"
4. Copia la password generata (16 caratteri)

### Passo 3: Usa la password nell'ambiente
```
MAIL_USERNAME=tuaemail@gmail.com
MAIL_PASSWORD=abcd efgh ijkl mnop
```

---

## 🧪 Test dopo il Deploy

### 1. Verifica homepage
```
curl https://tuo-sito.com
```

### 2. Test database
Accedi all'applicazione e crea un appuntamento di test

### 3. Test email
Crea un appuntamento e verifica che arrivi l'email

### 4. Test upload CV
Applica a un lavoro caricando un PDF

---

## 🔧 Troubleshooting

### Problema: "Application failed to start"
**Soluzione**: Controlla i log
```cmd
docker-compose logs app
```

### Problema: "Database connection failed"
**Soluzione**: Verifica DATABASE_URL, USERNAME, PASSWORD

### Problema: "Email non vengono inviate"
**Soluzione**: 
- Verifica App Password Gmail
- Controlla log: `docker-compose logs app | findstr mail`

### Problema: "Porta 8080 già in uso"
**Soluzione**: 
```cmd
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

---

## 📊 Monitoraggio

### Logs in tempo reale
```cmd
docker-compose logs -f app
```

### Spazio disco
```cmd
docker system df
```

### Backup database
```cmd
docker exec servizi-db pg_dump -U postgres servizi_edili > backup.sql
```

---

## 🎉 SOLUZIONE RAPIDA - INIZIA ORA!

**Il modo più veloce per andare online SUBITO (5 minuti)**:

1. **Installa ngrok**: https://ngrok.com/download
2. **Avvia app**: `docker-compose up -d`
3. **Esponi**: `ngrok http 8080`
4. **Condividi**: L'URL che ngrok ti dà

✅ Il tuo sito è ONLINE e accessibile da tutti!

---

## 📞 Supporto
Per problemi, controlla i log: `docker-compose logs app`
