package com.pulseiq.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;

/**
 * Firebase configuration that initializes FirebaseApp exclusively
 * from the FIREBASE_JSON environment variable.
 * The bean is only created if FIREBASE_JSON is defined.
 */
@Configuration
@ConditionalOnProperty(name = "FIREBASE_JSON")
public class FirebaseConfig {

    /**
     * The raw JSON string stored in the FIREBASE_JSON environment variable.
     */
    @Value("${FIREBASE_JSON:}")
    private String firebaseJson;

    @PostConstruct
    public void init() throws IOException {
        // Skip firebase initialization if no JSON provided
        if (firebaseJson == null || firebaseJson.isBlank()) {
            return;
        }

        try (InputStream serviceAccount = new ByteArrayInputStream(
                 firebaseJson.getBytes(StandardCharsets.UTF_8))) {

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
