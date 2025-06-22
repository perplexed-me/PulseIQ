package com.pulseiq.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

/**
 * Firebase configuration that initializes FirebaseApp using JSON from
 * an environment variable or a classpath resource.
 */
@Configuration
public class FirebaseConfig {

    /**
     * The raw JSON string stored in the FIREBASE_JSON environment variable.
     */
    @Value("${FIREBASE_JSON:}")
    private String firebaseJson;

    @PostConstruct
    public void init() throws IOException {
        InputStream serviceAccount;

        // Prefer JSON from environment variable for CI/containers
        if (!firebaseJson.isBlank()) {
            serviceAccount = new ByteArrayInputStream(
                firebaseJson.getBytes(StandardCharsets.UTF_8)
            );
        } else {
            // Fallback to classpath resource
            serviceAccount = getClass().getClassLoader()
                .getResourceAsStream("firebase-service-account.json");
        }

        if (serviceAccount == null) {
            throw new IllegalStateException(
                "Firebase service account JSON not provided via FIREBASE_JSON or classpath."
            );
        }

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
