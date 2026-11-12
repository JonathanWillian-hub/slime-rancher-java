@echo off
title Slime Rancher 2D - Iso Farm
cd /d "%~dp0"

echo ======================================================================
echo             SLIME RANCHER 2D (libGDX Isometria)
echo ======================================================================
echo.
echo Iniciando o jogo...
echo.

:: 1. Verifica se existe o JAR pre-compilado na pasta executavel
if exist "executavel\slime-farm-iso.jar" (
    echo [1/2] Executavel JAR standalone encontrado!
    where javaw >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        start "" javaw -Xms128m -Xmx768m -jar "executavel\slime-farm-iso.jar"
        exit /b 0
    )
    where java >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        java -Xms128m -Xmx768m -jar "executavel\slime-farm-iso.jar"
        exit /b 0
    )
)

:: 2. Verifica se existe na pasta de build do lwjgl3
if exist "lwjgl3\build\libs\slime-farm-iso-1.0.0.jar" (
    echo [1/2] Executavel JAR em build/libs encontrado!
    where javaw >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        start "" javaw -Xms128m -Xmx768m -jar "lwjgl3\build\libs\slime-farm-iso-1.0.0.jar"
        exit /b 0
    )
    where java >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        java -Xms128m -Xmx768m -jar "lwjgl3\build\libs\slime-farm-iso-1.0.0.jar"
        exit /b 0
    )
)

:: 3. Se nao rodou pelo JAR direto, executa via Gradle Wrapper
echo [2/2] Iniciando via Gradle Wrapper integrado (auto-compilacao)...
echo.
call .\gradlew.bat lwjgl3:run

if %ERRORLEVEL% neq 0 (
    echo.
    echo ======================================================================
    echo [AVISO] Nao foi possivel iniciar o jogo automaticamente.
    echo Certifique-se de que o Java 17+ (JDK/JRE) esta instalado.
    echo Se necessario, instale o Java 17 em: https://adoptium.net/
    echo ======================================================================
    echo.
    pause
)
