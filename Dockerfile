# ─────────────────────────────────────────────────────────────────────────────
# Stage 1: Build with Maven + JDK 21
# ─────────────────────────────────────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-jammy AS builder
WORKDIR /app
# 1) Copy the pom.xml first to leverage Docker cache
# This allows Docker to cache dependencies and avoid re-downloading them
# when only source code changes.
COPY pom.xml ./


RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn package -DskipTests -B

# ─────────────────────────────────────────────────────────────────────────────
# Stage 2: Run on a slim JRE 21 image
# ─────────────────────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy AS runtime


RUN addgroup --system appgroup && adduser --system --ingroup appgroup appuser

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar


RUN chown appuser:appgroup /app/app.jar


USER appuser


EXPOSE 8085


ENTRYPOINT ["java", "-jar", "/app/app.jar"]
