# Dockerfile
FROM eclipse-temurin:17-jdk-alpine

# Create working directory
WORKDIR /app

# Copy app jar (you must build it first)
COPY target/wallet.jar app.jar

# Expose port (optional)
EXPOSE 8080

# Run with profile 'prod'
ENTRYPOINT ["java", "-Dspring.profiles.active=prod", "-jar", "app.jar"]
