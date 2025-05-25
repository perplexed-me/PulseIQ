package com.pulseiq.controller;

import com.pulseiq.dto.*;
import com.pulseiq.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }
    @PostMapping("/register/doctor")
    public ResponseEntity<?> registerDoctor(@Valid @RequestBody DoctorRegistrationDto dto) {
        userService.registerDoctor(dto);
        return ResponseEntity.ok("Doctor registered successfully");
    }
    @PostMapping("/register/patient")
    public ResponseEntity<?> registerPatient(@Valid @RequestBody PatientRegistrationDto dto) {
        userService.registerPatient(dto);
        return ResponseEntity.ok("Patient registered successfully");
    }

    @PostMapping("/register/technician")
    public ResponseEntity<?> registerTechnician(@Valid @RequestBody TechnicianRegistrationDto dto) {
        userService.registerTechnician(dto);
        return ResponseEntity.ok("Technician registered successfully");
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