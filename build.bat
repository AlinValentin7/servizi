# Build script per Windows
@echo off
echo ========================================
echo  Building Servizi Edili Elvis SRL
echo ========================================
echo.

echo Cleaning previous builds...
call mvnw.cmd clean

echo.
echo Building application...
call mvnw.cmd package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  BUILD SUCCESSFUL!
    echo ========================================
    echo JAR file: target\servizi-0.0.1-SNAPSHOT.jar
    echo.
    echo To run the application:
    echo   java -jar target\servizi-0.0.1-SNAPSHOT.jar
    echo.
) else (
    echo.
    echo ========================================
    echo  BUILD FAILED!
    echo ========================================
    echo.
)
