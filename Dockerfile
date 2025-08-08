# Use OpenJDK 17 as base image
FROM eclipse-temurin:17-jdk

# Set working directory
WORKDIR /app

# Copy all files into the container
COPY . .

# Give execute permission to mvnw
RUN chmod +x mvnw

# Build the project
RUN ./mvnw clean package

# Run the app
CMD ["java", "-jar", "target/aihelper-0.0.1-SNAPSHOT.jar"]
