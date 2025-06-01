package com.pulseiq.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pulseiq.entity.User;
import com.pulseiq.entity.UserStatus;
import com.pulseiq.repository.UserRepository;

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
