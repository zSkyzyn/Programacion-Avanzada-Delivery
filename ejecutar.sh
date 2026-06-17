#!/bin/bash

echo "================================"
echo " Plataforma Delivery - TP3"
echo " Programacion Avanzada - SOLID/OCP"
echo "================================"
echo ""

# Verificar si Maven está instalado
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven (mvn) no está instalado o no se encuentra en el PATH."
    echo "Puedes instalarlo en sistemas basados en Debian/Ubuntu con: sudo apt install maven"
    exit 1
fi

# Compilar primero (descarga deps si es necesario)
echo "[1/2] Compilando el proyecto..."
mvn -q package -DskipTests assembly:single

if [ $? -ne 0 ]; then
    echo "ERROR: Falló la compilación. Revisa los errores arriba."
    exit 1
fi

echo "[2/2] Iniciando la aplicación..."
echo ""

java --add-opens=java.base/java.lang=ALL-UNNAMED \
     --add-opens=java.base/java.util=ALL-UNNAMED \
     -jar "target/plataforma-delivery-1.0-SNAPSHOT-jar-with-dependencies.jar"
