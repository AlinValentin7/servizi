@echo off
echo ============================================
echo   CREA DATABASE SERVIZI_EDILI
echo ============================================
echo.

REM Cerca MySQL in varie posizioni comuni
set MYSQL_PATH=
if exist "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" set MYSQL_PATH=C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe
if exist "C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe" set MYSQL_PATH=C:\Program Files (x86)\MySQL\MySQL Server 8.0\bin\mysql.exe
if exist "C:\MySQL\bin\mysql.exe" set MYSQL_PATH=C:\MySQL\bin\mysql.exe

if "%MYSQL_PATH%"=="" (
    echo [ERRORE] MySQL non trovato nei percorsi standard!
    echo.
    echo Cerca manualmente dove hai installato MySQL:
    echo - Apri Esplora File
    echo - Cerca mysql.exe
    echo - Copia il percorso completo qui sotto
    echo.
    set /p MYSQL_PATH="Inserisci il percorso completo di mysql.exe: "
)

if not exist "%MYSQL_PATH%" (
    echo [ERRORE] MySQL non trovato in: %MYSQL_PATH%
    echo.
    pause
    exit /b 1
)

echo [OK] MySQL trovato in: %MYSQL_PATH%
echo.

echo Creo il database servizi_edili...
"%MYSQL_PATH%" -u root -padmin1234 -e "CREATE DATABASE IF NOT EXISTS servizi_edili CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if %errorlevel% equ 0 (
    echo [OK] Database creato con successo!
    echo.
    echo Verifico...
    "%MYSQL_PATH%" -u root -padmin1234 -e "SHOW DATABASES LIKE 'servizi_edili';"
    echo.
    echo [OK] Database servizi_edili pronto all'uso!
) else (
    echo [ERRORE] Impossibile creare il database
    echo Verifica che MySQL800 sia in esecuzione
)

echo.
pause
