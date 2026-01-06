@echo off
chcp 65001 >nul
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  ☁️  DEPLOY GRATUITO 24/7 - SERVIZI EDILI                     ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
echo Scegli la piattaforma di hosting GRATUITO:
echo.
echo [1] 🚂 RAILWAY.APP - CONSIGLIATO! (Sempre attivo, $5/mese gratis)
echo [2] ☁️  RENDER.COM - Gratis per sempre (sleep dopo 15min)
echo [3] 📖 Leggi guida completa
echo [0] ❌ Torna indietro
echo.

set /p choice="Scegli (0-3): "

if "%choice%"=="1" goto railway
if "%choice%"=="2" goto render
if "%choice%"=="3" goto guida
if "%choice%"=="0" exit /b 0
echo Scelta non valida!
pause
exit /b 1

:railway
echo.
echo ════════════════════════════════════════
echo  🚂 DEPLOY SU RAILWAY.APP
echo ════════════════════════════════════════
echo.
echo Railway.app è la soluzione PIÙ SEMPLICE e GRATUITA!
echo.
echo ✅ $5 di credito gratuito al mese
echo ✅ Sempre attivo (NO sleep)
echo ✅ Deploy automatico da GitHub
echo ✅ Database PostgreSQL incluso
echo ✅ SSL/HTTPS automatico
echo.
echo 📋 PASSO 1: Prepara GitHub
echo ════════════════════════════════════════
echo.
echo Prima devi caricare il progetto su GitHub.
echo.
echo Hai già il progetto su GitHub? (S/N)
set /p github="Risposta: "

if /i "%github%"=="N" (
    echo.
    echo 📝 CARICA SU GITHUB PRIMA:
    echo.
    echo 1. Vai su https://github.com/new
    echo 2. Crea repository "servizi-edili"
    echo 3. Esegui questi comandi:
    echo.
    echo    cd C:\create\servizi
    echo    git init
    echo    git add .
    echo    git commit -m "Deploy su Railway"
    echo    git remote add origin https://github.com/TUO-USERNAME/servizi-edili.git
    echo    git branch -M main
    echo    git push -u origin main
    echo.
    echo Quando hai finito, riavvia questo script!
    echo.
    pause
    exit /b 0
)

echo.
echo 📋 PASSO 2: Deploy su Railway
echo ════════════════════════════════════════
echo.
echo Apro Railway.app nel browser...
echo.
echo COSA FARE:
echo 1. Accedi con GitHub
echo 2. New Project → Deploy from GitHub repo
echo 3. Seleziona "servizi-edili"
echo 4. Add PostgreSQL (pulsante in alto)
echo 5. Aggiungi variabili ambiente:
echo      SPRING_PROFILES_ACTIVE=prod
echo      MAIL_USERNAME=tuaemail@gmail.com
echo      MAIL_PASSWORD=tua-app-password
echo      WHATSAPP_NUMBER=+393801590128
echo 6. Deploy automatico!
echo.
echo Railway farà tutto automaticamente in 5 minuti!
echo.
pause

start https://railway.app
start "" "DEPLOY_GRATUITO.md"

echo.
echo ✅ Guida aperta nel browser!
pause
exit /b 0

:render
echo.
echo ════════════════════════════════════════
echo  ☁️  DEPLOY SU RENDER.COM
echo ════════════════════════════════════════
echo.
echo Render.com è GRATIS per sempre!
echo.
echo ✅ Completamente gratuito
echo ✅ SSL/HTTPS automatico
echo ✅ Database PostgreSQL gratis
echo ⚠️  Va in sleep dopo 15min (si risveglia in 30sec)
echo.
echo 📋 PASSO 1: Prepara GitHub
echo.
echo Hai già il progetto su GitHub? (S/N)
set /p github="Risposta: "

if /i "%github%"=="N" (
    echo.
    echo 📝 CARICA SU GITHUB PRIMA:
    echo.
    echo 1. Vai su https://github.com/new
    echo 2. Crea repository "servizi-edili"
    echo 3. Esegui questi comandi:
    echo.
    echo    cd C:\create\servizi
    echo    git init
    echo    git add .
    echo    git commit -m "Deploy su Render"
    echo    git remote add origin https://github.com/TUO-USERNAME/servizi-edili.git
    echo    git branch -M main
    echo    git push -u origin main
    echo.
    echo Quando hai finito, riavvia questo script!
    echo.
    pause
    exit /b 0
)

echo.
echo 📋 PASSO 2: Deploy su Render
echo ════════════════════════════════════════
echo.
echo Apro Render.com nel browser...
echo.
echo COSA FARE:
echo 1. Accedi con GitHub
echo 2. New + → PostgreSQL (crea database prima)
echo 3. New + → Web Service
echo 4. Seleziona repository "servizi-edili"
echo 5. Runtime: Docker
echo 6. Plan: Free
echo 7. Aggiungi variabili ambiente (vedi guida)
echo 8. Create Web Service
echo.
echo Render farà il build in 3-5 minuti!
echo.
pause

start https://render.com
start "" "DEPLOY_GRATUITO.md"

echo.
echo ✅ Guida aperta nel browser!
pause
exit /b 0

:guida
echo.
echo 📖 Apertura guida completa...
start "" "DEPLOY_GRATUITO.md"
pause
exit /b 0
