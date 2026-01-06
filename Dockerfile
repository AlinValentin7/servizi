# Usa un'immagine base con Maven per il build
FROM maven:3.9-eclipse-temurin-21-alpine AS build

# Imposta la directory di lavoro
WORKDIR /app

# Copia i file di configurazione Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Scarica le dipendenze (layer cachabile)
RUN mvn dependency:go-offline -B

# Copia il codice sorgente
COPY src ./src

# Compila l'applicazione
RUN mvn clean package -DskipTests

# Usa un'immagine più leggera per l'esecuzione
FROM eclipse-temurin:21-jre-alpine

# Installa dipendenze necessarie
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Europe/Rome /etc/localtime && \
    echo "Europe/Rome" > /etc/timezone

# Imposta la directory di lavoro
WORKDIR /app

# Copia il JAR compilato dallo stage di build
COPY --from=build /app/target/servizi-0.0.1-SNAPSHOT.jar app.jar

# Crea directory per uploads e logs
RUN mkdir -p /app/uploads /app/logs /app/data

# Crea un utente non-root per sicurezza
RUN addgroup -S spring && adduser -S spring -G spring && \
    chown -R spring:spring /app

USER spring:spring

# Esponi la porta dell'applicazione
EXPOSE 8080

# Variabili d'ambiente di default (sovrascrivibili)
ENV SPRING_PROFILES_ACTIVE=railway
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Comando per avviare l'applicazione
CMD ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-railway}"]
