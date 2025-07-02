# Build stage
FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

# Copy everything and build the JAR
COPY . .
RUN ./mvnw clean package -DskipTests

# Run stage
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]



# FROM openjdk:17-jdk-slim

# # Install netcat
# RUN apt-get update && apt-get install -y netcat && apt-get clean

# WORKDIR /app

# # Copy JAR and wait script
# COPY target/smart-finance-manager-0.0.1-SNAPSHOT.jar app.jar
# COPY wait-for-it.sh /wait-for-it.sh
# RUN chmod +x /wait-for-it.sh

# # Correct entrypoint: pass host and port separately!
# ENTRYPOINT ["/wait-for-it.sh", "mysql", "3306", "--", "java", "-jar", "app.jar"]













# # Start from OpenJDK base image
# FROM openjdk:17-jdk-slim
# Install netcat for wait-for-it.sh
# RUN apt-get update && apt-get install -y netcat && rm -rf /var/lib/apt/lists/*
# # RUN apt-get update && apt-get install -y netcat && apt-get clean
#
# # Create app directory
# WORKDIR /app
#
# # Copy JAR file into the container
# COPY target/smart-finance-manager-0.0.1-SNAPSHOT.jar app.jar
#
# # Copy wait-for-it script and make it executable
# COPY wait-for-it.sh /wait-for-it.sh
# RUN chmod +x /wait-for-it.sh
#
# # Set the entrypoint to wait for MySQL, then start the app
# ENTRYPOINT ["/wait-for-it.sh", "mysql", "3306", "--", "java", "-jar", "app.jar"]

# ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "java", "-jar", "app.jar"]



# # Use a lightweight Java image
# FROM openjdk:17-jdk-slim
#
# # Set the working directory inside the container
# WORKDIR /app
#
# # Copy the JAR file into the container
# # COPY target/smart-finance-manager-0.0.1-SNAPSHOT.jar
# COPY target/smart-finance-manager-0.0.1-SNAPSHOT.jar app.jar
#
# # Expose port (same as your Spring Boot server.port, usually 8080)
# # EXPOSE 8082
#
# # Run the app
# # ENTRYPOINT ["java", "-jar", "app.jar"]
#
#
# COPY wait-for-it.sh .
#
# RUN chmod +x wait-for-it.sh
#
# EXPOSE 8082
#
# ENTRYPOINT ["./wait-for-it.sh", "mysql", "3306", "--", "java", "-jar", "app.jar"]
#
