package com.pulseiq.service;


import com.pulseiq.dto.*;
import com.pulseiq.entity.*;

import com.pulseiq.repository.DoctorRepository;
import com.pulseiq.repository.PatientRepository;
import com.pulseiq.repository.TechnicianRepository;
import com.pulseiq.repository.UserRepository;
import com.pulseiq.security.JwtUtil;
import com.pulseiq.security.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.UserRecord;


@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository repo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private TechnicianRepository technicianRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired
    private UserDetailsServiceImpl userDetailsService;


    private String normalizePhone(String phone) {
        if (phone == null || phone.isBlank()) return null;
        phone = phone.trim();
        if (phone.matches("^01\\d{9}$")) {
            return "+88" + phone;
        } else if (phone.matches("^\\+8801\\d{9}$")) {
            return phone;
        } else {
            throw new IllegalArgumentException("Invalid Bangladeshi phone number format.");
        }
    }
    private String generateUserId(String prefix) {
        String yearMonth = new SimpleDateFormat("yyyyMM").format(new Date());
        String pattern = prefix + yearMonth;

        // Fetch max existing ID starting with this pattern (e.g., P202405)
        Optional<User> lastUser = repo.findTopByUserIdStartingWithOrderByUserIdDesc(pattern);

        int sequence = 1;
        if (lastUser.isPresent()) {
            String lastId = lastUser.get().getUserId(); // e.g., P202405007
            String lastSeq = lastId.substring(pattern.length()); // "007"
            sequence = Integer.parseInt(lastSeq) + 1;
        }

        return pattern + String.format("%03d", sequence); // e.g., P202405001
    }
    private String createAndSaveUser(RegisterRequest dto, UserRole role, UserStatus status, String userIdPrefix) {
        String userId = generateUserId(userIdPrefix);

        String normalizedEmail = dto.getEmail() != null ? dto.getEmail().trim().toLowerCase() : null;
        String normalizedPhone = normalizePhone(dto.getPhone());

        if (normalizedEmail == null && normalizedPhone == null) {
            throw new RuntimeException("Either email or phone must be provided.");
        }

        User user = new User();
        user.setUserId(userId);
        user.setUsername(normalizedEmail != null ? normalizedEmail : normalizedPhone);
        user.setEmail(normalizedEmail);
        user.setPhone(normalizedPhone);
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(role);
        user.setStatus(status);

        repo.save(user);
        return userId;
    }


    private void createDoctorProfile(String userId, DoctorRegistrationDto req) {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(userId);
        doctor.setFirstName(req.getFirstName());
        doctor.setLastName(req.getLastName());
        doctor.setSpecialization(req.getSpecialization());
        doctor.setDegree(req.getDegree());
        doctor.setLicenseNumber(req.getLicenseNumber());
        doctor.setAssistantName(req.getAssistantName());
        doctor.setAssistantNumber(req.getAssistantNumber());
        doctor.setConsultationFee(
                req.getConsultationFee() != null ? new BigDecimal(req.getConsultationFee()) : BigDecimal.ZERO
        );
        doctorRepo.save(doctor);
    }
    private void createPatientProfile(String userId, PatientRegistrationDto dto) {
        Patient patient = new Patient();
        patient.setPatientId(userId);
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setAge(dto.getAge());
        patient.setGender(dto.getGender());
        patient.setBloodGroup(dto.getBloodGroup());
        patient.setRegistrationDate(LocalDateTime.now());

        patientRepo.save(patient);
    }

    private void createTechnicianProfile(String userId, TechnicianRegistrationDto dto) {
        Technician technician = new Technician();
        technician.setTechnicianId(userId);
        technician.setFirstName(dto.getFirstName());
        technician.setLastName(dto.getLastName());
        technician.setSpecialization(dto.getSpecialization());

        technicianRepo.save(technician);
    }


    public void registerDoctor(DoctorRegistrationDto dto) {
        String userId = createAndSaveUser(dto, UserRole.DOCTOR, UserStatus.PENDING, "D");
        createDoctorProfile(userId, dto);
    }
    public void registerPatient(PatientRegistrationDto dto) {
        String userId = createAndSaveUser(dto, UserRole.PATIENT, UserStatus.ACTIVE, "P");
        createPatientProfile(userId, dto);
    }

    public void registerTechnician(TechnicianRegistrationDto dto) {
        String userId = createAndSaveUser(dto, UserRole.TECHNICIAN, UserStatus.PENDING, "T");
        createTechnicianProfile(userId, dto);
    }


    private User findUserByIdentifier(String identifier) {
        Optional<User> optionalUser = Optional.empty();

        if (identifier.matches("^(?i)[pdt]\\d+$")) {
            optionalUser = repo.findByUserIdIgnoreCase(identifier);
        } else if (identifier.matches("^\\d{11}$") || identifier.matches("^\\+8801\\d{9}$")) {
            if (identifier.startsWith("01")) {
                identifier = "+88" + identifier;
            }
            optionalUser = repo.findByPhone(identifier);
        } else if (identifier.contains("@")) {
            optionalUser = repo.findByEmailIgnoreCase(identifier.toLowerCase());
        }

        return optionalUser.orElseThrow(() -> new RuntimeException("User not found."));
    }



    @Override
    public Map<String, String> login(LoginRequest req) {
        String identifier = req.getIdentifier().trim();
        String rawPassword = req.getPassword();

        User user = findUserByIdentifier(identifier);

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not approved.");
        }

        if (!encoder.matches(rawPassword, user.getPassword())) {
            throw new RuntimeException("Invalid password.");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        return Map.of(
                "token", token,
                "userId", user.getUserId(),
                "role", user.getRole().name()
        );
    }


}

