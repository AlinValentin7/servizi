@echo off
REM Ripristina i file dall'archivio `archive\unused` alla posizione originale.
REM Esegui questo script dalla radice del progetto: C:\create\servizi

echo Ripristino in corso...
if exist archive\unused\tests (move /Y "archive\unused\tests" "src\test" ) else echo "no tests to restore"
if exist archive\unused\data (move /Y "archive\unused\data" "data" ) else echo "no data to restore"
if exist archive\unused\logs (move /Y "archive\unused\logs" "logs" ) else echo "no logs to restore"
if exist archive\unused\scripts (
  for %%s in (archive\unused\scripts\*.*) do move /Y "%%s" .\
) else echo "no scripts to restore"
if exist archive\unused\docs (
  for %%d in (archive\unused\docs\*.*) do move /Y "%%d" .\
) else echo "no docs to restore"

echo Ripristino completato.
pause
