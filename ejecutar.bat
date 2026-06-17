@echo off
echo ================================
echo  Plataforma Delivery - TP3
echo  Programacion Avanzada - SOLID/OCP
echo ================================
echo.

set MVN=C:\tools\maven\apache-maven-3.9.6\bin\mvn.cmd

REM Compilar primero (descarga deps si es necesario)
echo [1/2] Compilando el proyecto...
call "%MVN%" -q package -DskipTests assembly:single
if errorlevel 1 (
    echo ERROR: Fallo la compilacion. Revisa los errores arriba.
    pause
    exit /b 1
)

echo [2/2] Iniciando la aplicacion...
echo.
java --add-opens=java.base/java.lang=ALL-UNNAMED ^
     --add-opens=java.base/java.util=ALL-UNNAMED ^
     -jar "target\plataforma-delivery-1.0-SNAPSHOT-jar-with-dependencies.jar"
