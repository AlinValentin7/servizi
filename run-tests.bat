# Test script per Windows
@echo off
echo ========================================
echo  Running Tests - Servizi Edili Elvis SRL
echo ========================================
echo.

echo Running all tests...
call mvnw.cmd test

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo  ALL TESTS PASSED!
    echo ========================================
    echo.
) else (
    echo.
    echo ========================================
    echo  SOME TESTS FAILED!
    echo ========================================
    echo Check the output above for details.
    echo.
)

pause
