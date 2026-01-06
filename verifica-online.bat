@echo off
chcp 65001 >nul
echo.
echo ╔════════════════════════════════════════════════════════════════╗
echo ║  🔍 VERIFICA STATO SITO ONLINE                                ║
echo ╚════════════════════════════════════════════════════════════════╝
echo.

echo Controllo stato applicazione...
echo.

REM Verifica Docker
docker ps | findstr servizi-app >nul
if %errorlevel% equ 0 (
    echo ✅ Applicazione Docker: IN ESECUZIONE
) else (
    echo ❌ Applicazione Docker: NON ATTIVA
    echo.
    echo Vuoi avviarla? (S/N)
    set /p start="Scegli: "
    if /i "%start%"=="S" (
        docker-compose up -d
        timeout /t 5 /nobreak >nul
    )
)

echo.
echo ════════════════════════════════════════
echo  📊 INFORMAZIONI ACCESSO
echo ════════════════════════════════════════
echo.

echo 🏠 Accesso locale:
echo    http://localhost:8080
echo.

echo 🌐 Accesso da rete locale:
for /f "tokens=2 delims=:" %%a in ('ipconfig ^| findstr /c:"IPv4"') do (
    set ip=%%a
    set ip=!ip:~1!
    if not "!ip!"=="" echo    http://!ip!:8080
)

echo.
echo ════════════════════════════════════════
echo  🧪 TEST CONNESSIONE
echo ════════════════════════════════════════
echo.

echo Testo connessione locale...
curl -s -o nul -w "Status HTTP: %%{http_code}\n" http://localhost:8080 2>nul
if %errorlevel% equ 0 (
    echo ✅ Server risponde correttamente
) else (
    echo ⚠️  Server non raggiungibile
    echo    Controlla che Docker sia in esecuzione
)

echo.
echo ════════════════════════════════════════
echo  📋 CONTAINER DOCKER ATTIVI
echo ════════════════════════════════════════
echo.
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo.
echo ════════════════════════════════════════
echo  📝 ULTIMI LOG (ultimi 20 righe)
echo ════════════════════════════════════════
echo.
docker logs --tail 20 servizi-app 2>nul

echo.
echo.
echo Per vedere i log in tempo reale: docker-compose logs -f app
echo Per fermare l'applicazione: docker-compose down
echo Per riavviare: docker-compose restart
echo.
pause
