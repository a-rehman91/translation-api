# Use a lightweight JDK
FROM openjdk:17-jdk-slim

# Set working directory
WORKDIR /app

# Copy the built jar file
COPY target/translation-api-1.0.0.jar app.jar

# Expose the application port
EXPOSE 8881

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]
