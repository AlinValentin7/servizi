# Usa un'immagine con Maven e JDK per compilare ed eseguire
FROM maven:3.9-eclipse-temurin-21-alpine

# Installa dipendenze necessarie
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Europe/Rome /etc/localtime && \
    echo "Europe/Rome" > /etc/timezone

# Imposta la directory di lavoro
WORKDIR /app

# Copia tutti i file del progetto
COPY . .

# Compila l'applicazione
RUN mvn clean package -DskipTests

# Crea directory necessarie
RUN mkdir -p /app/uploads /app/logs /app/data

# Esponi la porta dell'applicazione
EXPOSE 8080

# Variabili d'ambiente
ENV SPRING_PROFILES_ACTIVE=railway
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando per avviare l'applicazione
CMD echo "🚀 Starting Servizi Edili Elvis SRL..." && \
    echo "📊 Java Version:" && java -version && \
    echo "📁 Files in target/:" && ls -la target/ && \
    echo "🔧 SPRING_PROFILES_ACTIVE: ${SPRING_PROFILES_ACTIVE}" && \
    echo "🔧 PORT: ${PORT}" && \
    echo "🔧 DATABASE_URL: ${DATABASE_URL:NOT_SET}" && \
    echo "🚀 Starting application..." && \
    java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar target/servizi-0.0.1-SNAPSHOT.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-railway}
