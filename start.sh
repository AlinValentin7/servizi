#!/bin/bash
# Script di avvio per Render.com

echo "🚀 Avvio applicazione su Render..."

# Usa la porta fornita da Render
export PORT=${PORT:-8080}

# Avvia l'applicazione
java -jar target/servizi-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
