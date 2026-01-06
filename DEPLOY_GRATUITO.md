# 🚀 GUIDA DEPLOY GRATUITO 24/7 - RENDER.COM

## ✅ GRATIS E SEMPRE ONLINE (con limitazioni accettabili)

**Render.com** offre hosting gratuito con queste caratteristiche:
- ✅ **GRATIS per sempre**
- ✅ **Pubblico 24/7** (sempre online)
- ⚠️ **Si addormenta dopo 15 minuti di inattività** (si risveglia in ~30 secondi alla prima visita)
- ✅ **750 ore gratuite al mese** (più che sufficienti)
- ✅ **Database PostgreSQL incluso**
- ✅ **Certificato SSL automatico** (https://)
- ✅ **Deploy automatico da Git**

---

## 📋 PASSO 1: PREPARA IL CODICE SU GITHUB

### 1.1 Crea un repository GitHub (se non l'hai già fatto)

Vai su https://github.com/new e crea un nuovo repository chiamato `servizi-edili`

### 1.2 Carica il progetto su GitHub

Apri il terminale nella cartella del progetto e esegui:

```cmd
cd C:\create\servizi

REM Inizializza git (se non l'hai già fatto)
git init

REM Aggiungi .gitignore
echo target/ > .gitignore
echo .env >> .gitignore
echo *.log >> .gitignore
echo data/*.db >> .gitignore

REM Aggiungi tutti i file
git add .

REM Commit
git commit -m "Deploy su Render.com"

REM Collega al repository GitHub (sostituisci TUO-USERNAME)
git remote add origin https://github.com/TUO-USERNAME/servizi-edili.git

REM Push
git branch -M main
git push -u origin main
```

---

## 📋 PASSO 2: DEPLOY SU RENDER.COM

### 2.1 Crea account su Render
1. Vai su https://render.com
2. Clicca "Get Started" 
3. Accedi con GitHub (CONSIGLIATO) o email

### 2.2 Crea il Database PostgreSQL
1. Dashboard → **New +** → **PostgreSQL**
2. Configurazione:
   - **Name**: `servizi-db`
   - **Database**: `servizi_edili`
   - **User**: `servizi_user`
   - **Region**: Frankfurt (più vicino all'Italia)
   - **Plan**: **Free** ✅
3. Clicca **Create Database**
4. **IMPORTANTE**: Copia e salva questi valori (li trovi in "Info"):
   - **Internal Database URL** (inizia con `postgresql://...`)
   - **External Database URL** (per connessioni esterne)

### 2.3 Crea il Web Service
1. Dashboard → **New +** → **Web Service**
2. Clicca **Connect a repository** → scegli il tuo repository `servizi-edili`
3. Configurazione:
   - **Name**: `servizi-edili`
   - **Region**: Frankfurt
   - **Branch**: `main`
   - **Runtime**: **Docker** ✅
   - **Instance Type**: **Free** ✅

### 2.4 Configura le variabili d'ambiente
Nella sezione "Environment Variables", aggiungi:

```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=<INTERNAL-DATABASE-URL-copiato-prima>
DATABASE_USERNAME=servizi_user
DATABASE_PASSWORD=<password-del-database>
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=tuaemail@gmail.com
MAIL_PASSWORD=tua-app-password-gmail
WHATSAPP_NUMBER=+393801590128
```

**Per DATABASE_URL, USERNAME e PASSWORD**: 
- Render li crea automaticamente quando crei il database
- Trovali nella pagina del database → "Connections" → "Internal Database URL"
- Puoi anche cliccare "Add from Database" e selezionare `servizi-db`

### 2.5 Deploy!
1. Clicca **Create Web Service**
2. Render farà automaticamente:
   - Download del codice da GitHub
   - Build con Docker
   - Deploy dell'applicazione
3. Attendi 3-5 minuti per il primo deploy

---

## 🎉 IL TUO SITO SARÀ ONLINE!

Render ti assegnerà un URL tipo:
```
https://servizi-edili.onrender.com
```

**Questo URL è PUBBLICO, GRATUITO e PERMANENTE!** 🚀

---

## ⚡ ALTERNATIVA PIÙ VELOCE: RAILWAY.APP

Se Render ti sembra complicato, prova **Railway.app**:

### Vantaggi Railway:
- ✅ Ancora più semplice di Render
- ✅ **$5 di credito gratuito al mese** (abbastanza per un sito piccolo)
- ✅ **NON va in sleep** (sempre attivo)
- ✅ Deploy più veloce

### Deploy su Railway:

1. Vai su https://railway.app
2. Accedi con GitHub
3. **New Project** → **Deploy from GitHub repo**
4. Seleziona il repository `servizi-edili`
5. Railway rileva automaticamente che è un progetto Java
6. Clicca **Add PostgreSQL** (automatico)
7. Aggiungi le variabili ambiente (email, WhatsApp)
8. Deploy automatico!

Railway ti darà un URL tipo:
```
https://servizi-edili-production.up.railway.app
```

---

## 🆚 CONFRONTO OPZIONI GRATUITE

| Caratteristica | Render.com | Railway.app | ngrok (attuale) |
|----------------|------------|-------------|-----------------|
| **Costo** | Gratis | $5/mese credito gratis | Gratis |
| **Sempre online** | Sì (sleep dopo 15min) | Sì (no sleep) | Solo se PC acceso |
| **SSL/HTTPS** | ✅ Auto | ✅ Auto | ✅ |
| **Database** | PostgreSQL gratis | PostgreSQL incluso | Devi gestirlo tu |
| **Setup** | 15 minuti | 10 minuti | 2 minuti |
| **Permanente** | ✅ | ✅ | ❌ URL cambia |
| **Ideale per** | Siti personali | Produzione piccola | Demo temporanee |

---

## 🎯 LA MIA RACCOMANDAZIONE

**Per iniziare GRATIS e senza limiti di tempo**: 
👉 **Railway.app** (più semplice, sempre attivo)

**Se Railway finisce il credito**:
👉 **Render.com** (completamente gratis, ma sleep dopo 15min)

**Per demo veloci senza deploy**:
👉 **ngrok** (quello che stai usando ora)

---

## 🚀 VUOI CHE TI AIUTI CON IL DEPLOY?

Posso aiutarti a:
1. Preparare il codice per GitHub
2. Configurare Render.com passo-passo
3. O configurare Railway.app (ancora più facile)

Dimmi quale preferisci e procediamo! 🎉
