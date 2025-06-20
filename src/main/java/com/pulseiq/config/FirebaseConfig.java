package com.pulseiq.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;   // or javax.annotation.PostConstruct
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void init() throws IOException {
        // Adjust this path if your JSON lives in a subfolder under resources
        try (InputStream serviceAccount =
                 getClass().getClassLoader().getResourceAsStream("firebase-service-account.json")) {
            if (serviceAccount == null) {
                throw new IllegalStateException(
                    "firebase-service-account.json not found."
                );
            }
            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

            // Avoid re-initializing if the app is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        }
    }
}
