
// package com.pulseiq.controller;

// import com.google.firebase.auth.FirebaseAuthException;
// import com.pulseiq.dto.*;
// import com.pulseiq.service.UserService;
// import jakarta.validation.Valid;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;

// import java.time.LocalDateTime;
// import java.util.HashMap;
// import java.util.Map;

// @RestController
// @RequestMapping("/api/auth")
// // @CrossOrigin(origins = "*")
// public class AuthController {

//     @Autowired
//     private UserService userService;

//     @PostMapping("/login")
//     public ResponseEntity<?> login(@RequestBody LoginRequest req) {
//         return ResponseEntity.ok(userService.login(req));
//     }
//     @PostMapping("/register/doctor")
//     public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRegistrationDto dto) {
//         userService.registerDoctor(dto);
//         return ResponseEntity.ok("Doctor registered successfully");
//     }
//     @PostMapping("/register/patient")
//     public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationDto dto) {
//         userService.registerPatient(dto);
//         return ResponseEntity.ok("Patient registered successfully");
//     }

//     @PostMapping("/register/technician")
//     public ResponseEntity<?> registerTechnician(@Valid @RequestBody TechnicianRegistrationDto dto) {
//         userService.registerTechnician(dto);
//         return ResponseEntity.ok("Technician registered successfully");
//     }
    

//     @PostMapping("/google-patient")
//     public ResponseEntity<?> loginWithGooglePatient(@RequestBody GoogleLoginRequest request) {
//         try {
//             Map<String, String> response = userService.loginWithGoogleAsPatient(request.getIdToken());
//             return ResponseEntity.ok(response);
//         } catch (FirebaseAuthException e) {
//             // ID token invalid or expired
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                                  .body(Map.of("error", "Invalid Google ID token"));
//         } catch (IllegalArgumentException e) {
//             // Role mismatch (already a doctor/technician/admin) or missing email
//             return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                                  .body(Map.of("error", e.getMessage()));
//         } catch (Exception e) {
//             // Any other internal error
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                                  .body(Map.of("error", e.getMessage()));
//         }
//     }






//     @GetMapping("/health")
//     public ResponseEntity<?> healthCheck() {
//         Map<String, String> response = new HashMap<>();
//         response.put("status", "UP");
//         response.put("message", "PulseIQ Hospital Management System API is running");
//         response.put("timestamp", LocalDateTime.now().toString());
//         return ResponseEntity.ok(response);
//     }
// }

package com.pulseiq.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseiq.dto.DoctorRegistrationDto;
import com.pulseiq.dto.GoogleLoginRequest;
import com.pulseiq.dto.LoginRequest;
import com.pulseiq.dto.PatientRegistrationDto;
import com.pulseiq.dto.TechnicianRegistrationDto;
import com.pulseiq.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            Map<String, Object> response = userService.login(req);
            // System.out.println(response.get("id"));
           
            // Check if the response contains status information (PENDING/REJECTED)
            if (response.containsKey("status")) {
                String status = (String) response.get("status");
                if ("PENDING".equals(status)) {
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                }
                if ("REJECTED".equals(status)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            //System.out.println("08");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
    
    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRegistrationDto dto) {
        try {
            userService.registerDoctor(dto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Doctor registration submitted successfully. Please wait for admin approval.");
            response.put("status", "PENDING");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationDto dto) {
        try {
            userService.registerPatient(dto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Patient registered successfully. You can now login.");
            response.put("status", "ACTIVE");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/register/technician")
    public ResponseEntity<?> registerTechnician(@Valid @RequestBody TechnicianRegistrationDto dto) {
        try {
            userService.registerTechnician(dto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Technician registration submitted successfully. Please wait for admin approval.");
            response.put("status", "PENDING");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    @PostMapping("/google-patient")
    public ResponseEntity<?> loginWithGooglePatient(@RequestBody GoogleLoginRequest request) {
        try {
            Map<String, Object> response = userService.loginWithGoogleAsPatient(request.getIdToken());
            
            // Check if the response contains status information (PENDING/REJECTED)
            if (response.containsKey("status")) {
                String status = (String) response.get("status");
                if ("PENDING".equals(status)) {
                    return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                }
                if ("REJECTED".equals(status)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Role mismatch (already a doctor/technician/admin) or missing email
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
        } catch (RuntimeException e) {
            // Invalid Google token or other authentication issues
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        } catch (Exception e) {
            // Any other internal error
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "PulseIQ Hospital Management System API is running");
        response.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(response);
    }
}