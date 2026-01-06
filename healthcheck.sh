#!/bin/sh
# Health check script per Railway

echo "🏥 Health Check - Servizi Edili Elvis SRL"
echo "Checking application health..."

# Aspetta che l'applicazione sia pronta
MAX_WAIT=120
WAIT=0

while [ $WAIT -lt $MAX_WAIT ]; do
    if curl -f -s http://localhost:${PORT:-8080}/actuator/health > /dev/null 2>&1; then
        echo "✅ Application is healthy!"
        exit 0
    fi
    
    if curl -f -s http://localhost:${PORT:-8080}/ > /dev/null 2>&1; then
        echo "✅ Application is responding!"
        exit 0
    fi
    
    echo "⏳ Waiting for application to start... ($WAIT/$MAX_WAIT seconds)"
    sleep 5
    WAIT=$((WAIT + 5))
done

echo "❌ Application failed to start within ${MAX_WAIT} seconds"
exit 1
