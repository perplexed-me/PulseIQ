package com.pulseiq.controller;

import com.pulseiq.entity.*;
import com.pulseiq.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired private UserRepository userRepo;

    @GetMapping("/pending")
    public List<User> getPending() {
        return userRepo.findAllByStatus(UserStatus.PENDING);
    }

    @GetMapping("/approved")
    public List<User> getApproved() {return userRepo.findAllByStatus(UserStatus.ACTIVE); }

    @GetMapping("/rejected")
    public List<User> getRejected() {return userRepo.findAllByStatus(UserStatus.REJECTED); }

    @PostMapping("/approve/{id}")
    public ResponseEntity<String> approve(@PathVariable Long id) {
        User user = userRepo.findById(id).orElseThrow();
        user.setStatus(UserStatus.ACTIVE);
        userRepo.save(user);
        return ResponseEntity.ok("User approved");
    }

    @PostMapping("/reject/{id}")
    public ResponseEntity<String> reject(@PathVariable Long id) {
        User user = userRepo.findById(id).orElseThrow();
        user.setStatus(UserStatus.REJECTED);
        userRepo.save(user);
        return ResponseEntity.ok("User rejected");
    }
}
