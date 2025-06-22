<<<<<<< HEAD
# PulseIQ

## Project Structure

```
PulseIQ
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── pulseiq
│   │   │           ├── config
│   │   │           ├── controller
│   │   │           ├── converter
│   │   │           ├── dto
│   │   │           ├── entity
│   │   │           ├── repository
│   │   │           ├── security
│   │   │           ├── service
│   │   │           └── PulseiqApplication.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── firebase-service-account.json
│   └── test
│       └── java
│           └── com
│               └── pulseiq
│                   └── PulseiqApplicationTests.java
└── README.md
```

---

## application.properties

Below is the content of `src/main/resources/application.properties`. Any sensitive values have been replaced with `***`.

```properties
# Hibernate naming strategy
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Remote (Neon) PostgreSQL (commented out)
# spring.datasource.url=jdbc:postgresql://ep-fancy-pine-a944h189-pooler.gwc.azure.neon.tech:5432/pulseiq_user?sslmode=require
# spring.datasource.username=***
# spring.datasource.password=***

# Local PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/pulseiq_db
spring.datasource.username=***
spring.datasource.password=***

spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.default_schema=PulseIQ

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=ERROR
#logging.level.org.hibernate.SQL=DEBUG

# Server configuration
server.port=8085
server.address=0.0.0.0

# HikariCP settings
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.initialization-fail-timeout=0

# Quote all identifiers globally
spring.jpa.properties.hibernate.globally_quoted_identifiers=true

# JWT configuration (keep secret values hidden)
jwt.secret=***
jwt.expiration=***(in milliseconds)

# CORS configuration
app.cors.allowed-origins=http://localhost:8080,http://192.168.89.164:8080
app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

### Notes

- Replace each `***` placeholder with the actual username, password, or secret when running locally.
- To switch to the hosted `Neon` database, uncomment the remote URL/credentials and comment out or remove the local settings.
- The `hibernate.ddl-auto=update` property will automatically adjust the database schema on startup. For production, consider changing this to `validate` or `none`.
- CORS is configured to allow requests from `http://localhost:8080` and `http://192.168.89.164:8080`. Adjust these origins as needed.
- The JWT expiration is set for `h` hours (in milliseconds). Modify if a different lifespan is required.
- Ensure that `firebase-service-account.json` is present in `src/main/resources` and configured correctly for `Firebase` integration.
=======
# PulseIQ

## Project Structure

```
PulseIQ
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com
│   │   │       └── pulseiq
│   │   │           ├── config
│   │   │           ├── controller
│   │   │           ├── converter
│   │   │           ├── dto
│   │   │           ├── entity
│   │   │           ├── repository
│   │   │           ├── security
│   │   │           ├── service
│   │   │           └── PulseiqApplication.java
│   │   └── resources
│   │       ├── application.properties
│   │       └── firebase-service-account.json
│   └── test
│       └── java
│           └── com
│               └── pulseiq
│                   └── PulseiqApplicationTests.java
└── README.md
```

---

## application.properties

Below is the content of `src/main/resources/application.properties`. Any sensitive values have been replaced with `***`.

```properties
# Hibernate naming strategy
spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl

# Remote (Neon) PostgreSQL (commented out)
# spring.datasource.url=jdbc:postgresql://ep-fancy-pine-a944h189-pooler.gwc.azure.neon.tech:5432/pulseiq_user?sslmode=require
# spring.datasource.username=***
# spring.datasource.password=***

# Local PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/pulseiq_db
spring.datasource.username=***
spring.datasource.password=***

spring.datasource.driver-class-name=org.postgresql.Driver
spring.jpa.properties.hibernate.default_schema=PulseIQ

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=ERROR
#logging.level.org.hibernate.SQL=DEBUG

# Server configuration
server.port=8085
server.address=0.0.0.0

# HikariCP settings
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.initialization-fail-timeout=0

# Quote all identifiers globally
spring.jpa.properties.hibernate.globally_quoted_identifiers=true

# JWT configuration (keep secret values hidden)
jwt.secret=***
jwt.expiration=***(in milliseconds)

# CORS configuration
app.cors.allowed-origins=http://localhost:8080,http://192.168.89.164:8080
app.cors.allowed-methods=GET,POST,PUT,DELETE,OPTIONS
```

### Notes

- Replace each `***` placeholder with the actual username, password, or secret when running locally.
- To switch to the hosted `Neon` database, uncomment the remote URL/credentials and comment out or remove the local settings.
- The `hibernate.ddl-auto=update` property will automatically adjust the database schema on startup. For production, consider changing this to `validate` or `none`.
- CORS is configured to allow requests from `http://localhost:8080` and `http://192.168.89.164:8080`. Adjust these origins as needed.
- The JWT expiration is set for `h` hours (in milliseconds). Modify if a different lifespan is required.
- Ensure that `firebase-service-account.json` is present in `src/main/resources` and configured correctly for `Firebase` integration.
>>>>>>> backend
