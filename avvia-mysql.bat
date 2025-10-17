@echo off
echo ============================================
echo   AVVIO SERVIZI EDILI ELVIS SRL CON MYSQL
echo ============================================
echo.

REM Cerca MySQL in varie posizioni comuni
set MYSQL_PATH=
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
if exist "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "C:\MySQL\bin\mysql.exe" set MYSQL_PATH=C:\MySQL\bin\mysql.exe

if "%MYSQL_PATH%"=="" (
    echo [AVVISO] MySQL non trovato automaticamente
    echo Salto la verifica del database
    echo.
    goto :skip_mysql_check
)

echo [0] MySQL trovato in: %MYSQL_PATH%
echo.

echo [1/4] Verifico servizio MySQL800...
sc query MySQL800 | find "RUNNING" >nul
if %errorlevel% equ 0 (
    echo [OK] MySQL800 e' gia' attivo!
) else (
    echo [AVVIO] Avvio MySQL800...
    net start MySQL800
    if %errorlevel% equ 0 (
        echo [OK] MySQL800 avviato con successo!
    ) else (
        echo [ERRORE] Impossibile avviare MySQL800
        echo Verifica che MySQL sia installato correttamente
        pause
        exit /b 1
    )
)
echo.

echo [2/4] Verifico database servizi_edili...
"%MYSQL_PATH%" -u root -padmin1234 -e "CREATE DATABASE IF NOT EXISTS servizi_edili;" 2>nul
if %errorlevel% equ 0 (
    echo [OK] Database servizi_edili pronto!
) else (
    echo [AVVISO] Verifica manualmente il database
)
echo.

:skip_mysql_check

echo [3/4] Compilo il progetto...
call mvnw clean package -DskipTests
if %errorlevel% neq 0 (
    echo [ERRORE] Compilazione fallita!
    pause
    exit /b 1
)
echo [OK] Compilazione completata!
echo.

echo [4/4] Avvio l'applicazione con profilo MySQL...
echo.
echo ============================================
echo   APPLICAZIONE IN ESECUZIONE
echo   URL: http://localhost:8080
echo   Demo Animazioni: http://localhost:8080/demo-animazioni
echo   Premi CTRL+C per fermare
echo ============================================
echo.

call mvnw spring-boot:run -Dspring-boot.run.profiles=mysql

pause
