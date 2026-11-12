@echo off
title Slime Rancher 2D - Iso Farm
cd /d "%~dp0"

echo ======================================================================
echo             SLIME RANCHER 2D (libGDX Isometria)
echo ======================================================================
echo.
echo Iniciando o jogo...
echo.

where javaw >nul 2>nul
if %ERRORLEVEL% equ 0 (
    start "" javaw -Xms128m -Xmx768m -jar "slime-farm-iso.jar"
    exit /b 0
)

where java >nul 2>nul
if %ERRORLEVEL% equ 0 (
    java -Xms128m -Xmx768m -jar "slime-farm-iso.jar"
    exit /b 0
)

echo [AVISO] Java nao encontrado no PATH do sistema.
echo Por favor, instale o Java 17+ em: https://adoptium.net/
pause
