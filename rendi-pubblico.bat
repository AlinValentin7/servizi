@echo off
chcp 65001 >nul
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  🌐 RENDI IL SITO PUBBLICO - SERVIZI EDILI                    ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.
echo ✅ L'applicazione è in esecuzione su http://localhost:8080
echo.
echo Scegli come renderlo accessibile:
echo.
echo [1] 🏠 Solo dalla mia rete WiFi (condividi IP locale)
echo [2] 🌐 Pubblico su Internet con ngrok (CONSIGLIATO)
echo [0] ❌ Esci
echo.

set /p choice="Scegli (0-2): "

if "%choice%"=="1" goto locale
if "%choice%"=="2" goto ngrok
if "%choice%"=="0" goto end
echo Scelta non valida!
pause
exit /b 1

:locale
echo.
echo ════════════════════════════════════════
echo  🏠 ACCESSO RETE LOCALE
echo ════════════════════════════════════════
echo.

echo 🔧 Apertura porta nel firewall...
netsh advfirewall firewall delete rule name="Servizi Edili" >nul 2>&1
netsh advfirewall firewall add rule name="Servizi Edili" dir=in action=allow protocol=TCP localport=8080 >nul

echo.
echo ✅ Firewall configurato!
echo.
echo 📍 Il sito è accessibile da:
echo.
echo    Dal tuo PC:
echo    http://localhost:8080
echo.
echo    Da altri dispositivi sulla tua rete WiFi:

setlocal enabledelayedexpansion
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set ip=%%a
    set ip=!ip:~1!
    if not "!ip!"=="" (
        if not "!ip!"=="127.0.0.1" (
            echo    http://!ip!:8080
        )
    )
)

echo.
echo ════════════════════════════════════════
echo  ℹ️  ISTRUZIONI
echo ════════════════════════════════════════
echo.
echo 1. Copia uno degli indirizzi IP sopra
echo 2. Connettiti alla stessa rete WiFi dal telefono/tablet
echo 3. Apri il browser e vai all'indirizzo
echo.
echo ⚠️  NOTA: Funziona solo se sei sulla stessa rete WiFi
echo.
pause
goto end

:ngrok
echo.
echo ════════════════════════════════════════
echo  🌐 PUBBLICO CON NGROK
echo ════════════════════════════════════════
echo.

REM Verifica se ngrok è installato
where ngrok >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ ngrok non è installato!
    echo.
    echo 📥 INSTALLAZIONE RAPIDA:
    echo.
    echo 1. Vai su: https://ngrok.com/download
    echo 2. Scarica ngrok per Windows
    echo 3. Estrai ngrok.exe in una cartella (es: C:\ngrok)
    echo 4. Aggiungi la cartella al PATH o metti ngrok.exe qui
    echo.
    echo Oppure usa chocolatey:
    echo    choco install ngrok
    echo.
    echo Oppure winget:
    echo    winget install ngrok
    echo.
    echo Apro la pagina di download...
    start https://ngrok.com/download
    echo.
    pause
    goto end
)

echo ✅ ngrok trovato!
echo.
echo 🚀 Avvio tunnel pubblico...
echo.
echo ════════════════════════════════════════════════════════════════
echo  IL TUO SITO SARÀ PUBBLICO E ACCESSIBILE DA CHIUNQUE!
echo.
echo  Copia l'URL "https://xxxxx.ngrok.io" che apparirà sotto
echo  e condividilo con chiunque vuoi.
echo.
echo  ⚠️  Per fermare: Premi CTRL+C
echo ════════════════════════════════════════════════════════════════
echo.
pause

ngrok http 8080

goto end

:end
echo.
echo 👋 Script terminato.
exit /b 0
