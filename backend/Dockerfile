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
# Stage 1 – Build the jar with Maven (Optimized for Docker layer caching)
###############################################################################
FROM maven:3.9.7-eclipse-temurin-21 AS build

# Set Maven options for better performance and stability
ENV MAVEN_OPTS="-Xmx1024m -XX:MaxMetaspaceSize=512m"

WORKDIR /build

# Copy Maven configuration first for better layer caching
# Only rebuild dependencies if pom.xml changes
COPY pom.xml .

# Download dependencies with timeout and better error handling
RUN mvn dependency:resolve -B -q --fail-at-end

# Copy source code (Firebase service account file is already included in src/main/resources/)
COPY src ./src

# Build with explicit memory settings and timeout
RUN mvn -B clean package -DskipTests \
  -Dmaven.wagon.http.retryHandler.count=3 \
  -Dmaven.wagon.httpconnectionManager.ttlSeconds=120 \
  -Dorg.slf4j.simplelogger.log.org.apache.maven.cli.transfer.Slf4jMavenTransferListener=warn

###############################################################################
# Stage 2 – Runtime image with security best practices
###############################################################################
FROM eclipse-temurin:21-jre-jammy

# Install curl for health checks
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Create non-root user for security
RUN groupadd -r appgroup && useradd -r -g appgroup appuser

WORKDIR /app

# Copy built JAR with proper ownership
COPY --from=build --chown=appuser:appgroup /build/target/*.jar app.jar

# Switch to non-root user
USER appuser

EXPOSE 8085

# Health check using curl
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:8085/actuator/health || exit 1

ENTRYPOINT ["java", "-jar", "/app/app.jar"]


