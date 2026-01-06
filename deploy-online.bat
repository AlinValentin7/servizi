@echo off
chcp 65001 >nul
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  🚀 DEPLOY ONLINE - SERVIZI EDILI ELVIS SRL                   ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

:menu
echo.
echo Scegli come vuoi mettere il sito online:
echo.
echo [1] 🏠 LOCALE - Solo dalla mia rete WiFi (test locale)
echo [2] 🌐 NGROK - Pubblico temporaneo (CONSIGLIATO per iniziare)
echo [3] ☁️  RENDER.COM - Hosting gratuito permanente
echo [4] 🚂 RAILWAY.APP - Hosting gratuito con credito
echo [5] 📖 Leggi la guida completa
echo [0] ❌ Esci
echo.

set /p choice="Scegli un'opzione (0-5): "

if "%choice%"=="1" goto local
if "%choice%"=="2" goto ngrok
if "%choice%"=="3" goto render
if "%choice%"=="4" goto railway
if "%choice%"=="5" goto guide
if "%choice%"=="0" goto end
echo Scelta non valida!
goto menu

:local
echo.
echo ════════════════════════════════════════
echo  🏠 DEPLOY LOCALE
echo ════════════════════════════════════════
echo.

REM Verifica se esiste .env
if not exist .env (
    echo ⚠️  File .env non trovato!
    echo Creo .env da .env.example...
    copy .env.example .env
    echo.
    echo ⚠️  IMPORTANTE: Modifica .env con i tuoi dati reali!
    echo    Premi un tasto quando hai finito...
    pause >nul
)

echo 📦 Avvio applicazione con Docker...
docker-compose up -d

if %errorlevel% neq 0 (
    echo.
    echo ❌ Errore durante l'avvio!
    echo Assicurati che Docker Desktop sia in esecuzione.
    pause
    goto menu
)

echo.
echo ✅ Applicazione avviata!
echo.
echo 🔧 Apertura porta nel firewall...
netsh advfirewall firewall delete rule name="Servizi Edili" >nul 2>&1
netsh advfirewall firewall add rule name="Servizi Edili" dir=in action=allow protocol=TCP localport=8080 >nul

echo.
echo 📍 Trova il tuo IP locale:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set ip=%%a
    set ip=!ip:~1!
    if not "!ip!"=="" echo    http://!ip!:8080
)

echo.
echo ════════════════════════════════════════
echo  ✅ SITO ONLINE SULLA TUA RETE!
echo ════════════════════════════════════════
echo.
echo Dal tuo PC:
echo    http://localhost:8080
echo.
echo Da altri dispositivi sulla tua rete WiFi:
echo    Usa uno degli IP sopra
echo.
echo Per vedere i log:
echo    docker-compose logs -f app
echo.
pause
goto menu

:ngrok
echo.
echo ════════════════════════════════════════
echo  🌐 DEPLOY CON NGROK
echo ════════════════════════════════════════
echo.

REM Controlla se ngrok è installato
where ngrok >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ngrok non trovato!
    echo.
    echo 📥 Scarica ngrok da: https://ngrok.com/download
    echo.
    echo Dopo averlo installato:
    echo 1. Registrati su https://dashboard.ngrok.com/signup
    echo 2. Copia il tuo authtoken
    echo 3. Esegui: ngrok config add-authtoken TUO_TOKEN
    echo 4. Riprova questo script
    echo.
    start https://ngrok.com/download
    pause
    goto menu
)

REM Verifica se l'app è in esecuzione
docker ps | findstr servizi-app >nul
if %errorlevel% neq 0 (
    echo 📦 Avvio applicazione...
    docker-compose up -d
    echo ⏳ Attendo 10 secondi per l'avvio...
    timeout /t 10 /nobreak >nul
)

echo.
echo ✅ Apertura tunnel pubblico con ngrok...
echo.
echo ════════════════════════════════════════
echo  Il tuo sito sarà accessibile a tutti!
echo  Copia l'URL https://xxxxx.ngrok.io
echo  e condividilo con chi vuoi.
echo ════════════════════════════════════════
echo.
echo ⚠️  Premi CTRL+C per chiudere il tunnel
echo.

ngrok http 8080

goto menu

:render
echo.
echo ════════════════════════════════════════
echo  ☁️  DEPLOY SU RENDER.COM
echo ════════════════════════════════════════
echo.
echo Per deployare su Render.com:
echo.
echo 1. Vai su https://render.com e crea un account (gratis)
echo 2. Crea un database PostgreSQL (Free tier)
echo 3. Crea un Web Service dal tuo repository GitHub
echo 4. Seleziona "Docker" come ambiente
echo 5. Aggiungi le variabili d'ambiente dal file .env
echo.
echo Apro la guida completa...
echo.
start https://render.com
start "" "GUIDA_DEPLOY_ONLINE.md"
pause
goto menu

:railway
echo.
echo ════════════════════════════════════════
echo  🚂 DEPLOY SU RAILWAY.APP
echo ════════════════════════════════════════
echo.
echo Per deployare su Railway:
echo.
echo 1. Vai su https://railway.app e accedi con GitHub
echo 2. New Project → Deploy from GitHub repo
echo 3. Aggiungi database PostgreSQL
echo 4. Railway configura automaticamente le variabili DB
echo 5. Aggiungi le altre variabili (email, WhatsApp)
echo.
echo Apro la guida completa...
echo.
start https://railway.app
start "" "GUIDA_DEPLOY_ONLINE.md"
pause
goto menu

:guide
echo.
echo 📖 Apertura guida completa...
start "" "GUIDA_DEPLOY_ONLINE.md"
pause
goto menu

:end
echo.
echo 👋 Arrivederci!
exit /b 0
