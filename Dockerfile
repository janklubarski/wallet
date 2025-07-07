FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY target/wallet.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "app.jar"]