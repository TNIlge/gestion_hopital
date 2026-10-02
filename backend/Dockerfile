# ==============================================================================
# Dockerfile Multi-stage pour le Backend Spring Boot (Java 21)
# ==============================================================================

# Étape 1 : Compilation et packaging avec Maven
FROM maven:3.9.9-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Cache des dépendances Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copie des sources et packaging en JAR exécutable (skip tests pour le build d'image)
COPY src ./src
RUN mvn clean package -DskipTests

# ==============================================================================
# Étape 2 : Image d'exécution légère et sécurisée (JRE 21 Alpine)
# ==============================================================================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Sécurité : Création d'un utilisateur non-root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Récupération du binaire JAR généré
COPY --from=build /app/target/*.jar app.jar

RUN chown -R appuser:appgroup /app
USER appuser

# Port d'écoute de l'API REST Spring Boot
EXPOSE 8080

# Options JVM adaptées aux conteneurs
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Commande de démarrage
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
