# # ─────────────────────────────────────────────────────────────
# # Stage 1: Build with Maven and JDK 21
# # ─────────────────────────────────────────────────────────────
# FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder

# WORKDIR /app

# # Leverage Docker cache for dependencies
# COPY pom.xml .
# # RUN mvn dependency:go-offline -B

# # Copy source files and build the app
# COPY src ./src
# RUN mvn clean package -DskipTests -B

# # ─────────────────────────────────────────────────────────────
# # Stage 2: Runtime with slim JRE
# # ─────────────────────────────────────────────────────────────
# FROM eclipse-temurin:21-jre-jammy AS runtime

# # Create a non-root user for security
# RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

# WORKDIR /app

# # Copy built JAR from builder stage
# COPY --chown=appuser:appgroup --from=builder /app/target/*.jar app.jar

# USER appuser

# # Expose the Spring Boot port
# EXPOSE 8085

# # Run the Spring Boot application
# ENTRYPOINT ["java", "-jar", "/app/app.jar"]


FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY target/*.jar app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

