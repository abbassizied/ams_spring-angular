# Stage 1: Build the application
FROM maven:3.8.5-openjdk-17-slim AS builder

# Set the working directory
WORKDIR /app

# Copy the pom.xml and download the dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the source code and build the application
COPY src ./src
RUN mvn package -DskipTests

# Stage 2: Run the application using a distroless image
FROM gcr.io/distroless/java17-debian11

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar /app/app.jar

# Specify the entry point to run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]