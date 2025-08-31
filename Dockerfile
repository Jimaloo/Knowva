# Build stage
FROM gradle:8.5-jdk17 AS build

WORKDIR /app

# Copy only necessary files for server build
COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY settings.gradle.kts build.gradle.kts gradle.properties ./
COPY shared/ shared/
COPY server/ server/

# Make gradlew executable
RUN chmod +x ./gradlew

# Build only the server module and create fat jar
RUN ./gradlew server:buildFatJar -x test --no-daemon

# Runtime stage
FROM openjdk:17-jre-slim

WORKDIR /app

# Copy the built server jar
COPY --from=build /app/server/build/libs/server-*-all.jar app.jar

# Expose the port
EXPOSE 8080

# Set environment variables
ENV KTOR_ENV=production
ENV PORT=8080

# Run the application
CMD ["java", "-jar", "app.jar"]