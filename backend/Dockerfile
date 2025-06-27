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

# .............

# FROM eclipse-temurin:21-jre-jammy

# WORKDIR /app

# COPY target/*.jar app.jar

# EXPOSE 8085

# ENTRYPOINT ["java", "-jar", "/app/app.jar"]

# -------------

###############################################################################
# Stage 1 – Build the jar with Maven
###############################################################################
FROM maven:3.9.7-eclipse-temurin-21 AS build

WORKDIR /build

# Copy Maven configuration first for better layer caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn -B clean package -DskipTests

###############################################################################
# Stage 2 – Runtime image with security best practices
###############################################################################
FROM eclipse-temurin:21-jre-jammy

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copy built JAR with proper ownership
COPY --from=build --chown=appuser:appgroup /build/target/*.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8085

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]


