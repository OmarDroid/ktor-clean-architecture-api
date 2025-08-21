# Build stage with full Gradle environment
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Cache dependencies layer
COPY gradle/ gradle/
COPY gradlew gradlew.bat gradle.properties settings.gradle.kts build.gradle.kts ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Build application
COPY src/ src/
RUN ./gradlew buildFatJar --no-daemon

# Production stage with minimal JRE
FROM eclipse-temurin:17-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Security: non-root user
RUN groupadd -r ktor && useradd -r -g ktor ktor
WORKDIR /app
COPY --from=build /app/build/libs/*-all.jar app.jar
RUN chown -R ktor:ktor /app
USER ktor

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]