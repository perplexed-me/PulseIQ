package com.pulseiq.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pulseiq.entity.*;
import com.pulseiq.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.pulseiq.dto.DoctorRegistrationDto;
import com.pulseiq.dto.LoginRequest;
import com.pulseiq.dto.PatientRegistrationDto;
import com.pulseiq.dto.RegisterRequest;
import com.pulseiq.dto.TechnicianRegistrationDto;
import com.pulseiq.security.JwtUtil;
import com.pulseiq.security.UserDetailsServiceImpl;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    @Autowired private UserRepository repo;
    @Autowired private DoctorRepository doctorRepo;
    @Autowired private PatientRepository patientRepo;
    @Autowired private TechnicianRepository technicianRepo;
    @Autowired private PasswordEncoder encoder;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private UserDetailsServiceImpl userDetailsService;

    @Autowired private RegistrationDataRepository registrationDataRepo;
    @Autowired private ObjectMapper objectMapper; // Add to your dependencies

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

        Optional<User> lastUser = repo.findTopByUserIdStartingWithOrderByUserIdDesc(pattern);

        int sequence = 1;
        if (lastUser.isPresent()) {
            String lastId = lastUser.get().getUserId();
            String lastSeq = lastId.substring(pattern.length());
            sequence = Integer.parseInt(lastSeq) + 1;
        }

        return pattern + String.format("%03d", sequence);
    }

    // CORRECTED: Only create User entity, don't create profile tables yet
    private String createAndSaveUserOnly(RegisterRequest dto, UserRole role, UserStatus status, String userIdPrefix) {
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


    // UPDATED: Store registration data properly
    private void storeRegistrationData(String userId, Object registrationDto, UserRole role) {
        try {
            String jsonData = objectMapper.writeValueAsString(registrationDto);
            RegistrationData regData = new RegistrationData(userId, jsonData, role);
            registrationDataRepo.save(regData);
            System.out.println("Successfully stored registration data for user: " + userId);
        } catch (Exception e) {
            System.err.println("Failed to store registration data for user: " + userId);
            throw new RuntimeException("Failed to store registration data", e);
        }
    }

    // NEW: Retrieve registration data
    private Object getRegistrationData(String userId, UserRole role) {
        Optional<RegistrationData> regDataOpt = registrationDataRepo.findByUserId(userId);
        if (!regDataOpt.isPresent()) {
            throw new RuntimeException("Registration data not found for user: " + userId);
        }

        try {
            String jsonData = regDataOpt.get().getRegistrationJson();
            switch (role) {
                case DOCTOR:
                    return objectMapper.readValue(jsonData, DoctorRegistrationDto.class);
                case TECHNICIAN:
                    return objectMapper.readValue(jsonData, TechnicianRegistrationDto.class);
                case PATIENT:
                    return objectMapper.readValue(jsonData, PatientRegistrationDto.class);
                default:
                    throw new RuntimeException("Unsupported role: " + role);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve registration data for user: " + userId, e);
        }
    }

    // CORRECTED: Registration methods now only create User, not profile tables
    public void registerDoctor(DoctorRegistrationDto dto) {
        String userId = createAndSaveUserOnly(dto, UserRole.DOCTOR, UserStatus.PENDING, "D");
        storeRegistrationData(userId, dto, UserRole.DOCTOR);
        // Profile will be created only after admin approval
    }

    public void registerPatient(PatientRegistrationDto dto) {
        String userId = createAndSaveUserOnly(dto, UserRole.PATIENT, UserStatus.ACTIVE, "P"); // Patient auto-approved
        createPatientProfile(userId, dto); // Create profile immediately for patients
    }

    public void registerTechnician(TechnicianRegistrationDto dto) {
        String userId = createAndSaveUserOnly(dto, UserRole.TECHNICIAN, UserStatus.PENDING, "T");
        storeRegistrationData(userId, dto, UserRole.TECHNICIAN);
        // Profile will be created only after admin approval
    }


    @Transactional
    public void approveUser(String userId) {
        System.out.println("=== APPROVAL DEBUG START ===");
        System.out.println("Approving user: " + userId);

        try {
            // Fetch the user from the database
            Optional<User> optUser = repo.findByUserIdIgnoreCase(userId);
            if (!optUser.isPresent()) {
                System.out.println("ERROR: User not found: " + userId);
                throw new RuntimeException("User not found: " + userId);
            }

            User user = optUser.get();
            System.out.println("Found user with status: " + user.getStatus());
            System.out.println("User role: " + user.getRole());

            // Prevent approving users who are already active
            if (user.getStatus() == UserStatus.ACTIVE) {
                throw new RuntimeException("User is already approved and active.");
            }

            // Only allow approval from PENDING or REJECTED status
            if (user.getStatus() != UserStatus.PENDING && user.getStatus() != UserStatus.REJECTED) {
                System.out.println("ERROR: Cannot approve user with status: " + user.getStatus());
                throw new RuntimeException("User cannot be approved from status: " + user.getStatus());
            }

            // 1) First, update the user status to ACTIVE
            user.setStatus(UserStatus.ACTIVE);
            repo.save(user);
            System.out.println("User status updated to ACTIVE");

            // 2) Try to retrieve registration data for profile creation
            Optional<RegistrationData> maybeData = registrationDataRepo.findByUserId(userId);
            if (maybeData.isPresent()) {
                // Registration data exists, so we proceed to create the profile
                String json = maybeData.get().getRegistrationJson();
                switch(user.getRole()) {
                    case DOCTOR:
                        DoctorRegistrationDto dr = objectMapper.readValue(json, DoctorRegistrationDto.class);
                        createDoctorProfile(userId, dr); // Creating the doctor profile
                        System.out.println("Doctor profile created");
                        break;
                    case TECHNICIAN:
                        TechnicianRegistrationDto tr = objectMapper.readValue(json, TechnicianRegistrationDto.class);
                        createTechnicianProfile(userId, tr); // Creating the technician profile
                        System.out.println("Technician profile created");
                        break;
                    case PATIENT:
                        // Patients are auto-approved so we won’t hit this case during approval
                        System.out.println("Patients do not require approval here.");
                        break;
                    default:
                        System.out.println("Unsupported role for profile creation: " + user.getRole());
                        break;
                }

                // Clean up the registration data after successful profile creation
                registrationDataRepo.deleteByUserId(userId);
                System.out.println("Registration data cleaned up");
            } else {
                // If registration data is missing, log a warning but do not stop the process
                System.out.println("Warning: No registration data found for user: " + userId);
            }

            System.out.println("=== APPROVAL SUCCESS ===");

        } catch (Exception e) {
            System.err.println("=== APPROVAL FAILED ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to approve user: " + e.getMessage(), e);
        }
    }


    public void rejectUser(String userId) {
        Optional<User> optUser = repo.findByUserIdIgnoreCase(userId);
        if (!optUser.isPresent()) {
            throw new RuntimeException("User not found");
        }

        User user = optUser.get();
        
        // Prevent rejecting already active users
        // if (user.getStatus() == UserStatus.ACTIVE) {
        //     throw new RuntimeException("Cannot reject an active user.You may need to deactivate the user first.");
        // }
        
        // Allow rejection from PENDING status, or re-rejection if needed
        if (user.getStatus() == UserStatus.REJECTED) {
            System.out.println("User is already rejected, no action needed");
            return; // Or throw exception if you don't want to allow this
        }

        user.setStatus(UserStatus.REJECTED);
        repo.save(user);
        
        System.out.println("User " + userId + " has been rejected");
    }

    // Helper method to check if profile exists
    private boolean checkIfProfileExists(String userId, UserRole role) {
        switch (role) {
            case DOCTOR:
                return doctorRepo.findById(userId).isPresent();
            case TECHNICIAN:
                return technicianRepo.findById(userId).isPresent();
            case PATIENT:
                return patientRepo.findById(userId).isPresent();
            default:
                return false;
        }
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

    // private User findUserByIdentifier(String identifier) {
    //     Optional<User> optionalUser = Optional.empty();

    //     if (identifier.matches("^(?i)[dptn]\\d+$")) { // Added 'n' for admin if needed
    //         optionalUser = repo.findByUserIdIgnoreCase(identifier);
    //     } else if (identifier.matches("^\\d{11}$") || identifier.matches("^\\+8801\\d{9}$")) {
    //         if (identifier.startsWith("01")) {
    //             identifier = "+88" + identifier;
    //         }
    //         optionalUser = repo.findByPhone(identifier);
    //     } else if (identifier.contains("@")) {
    //         optionalUser = repo.findByEmailIgnoreCase(identifier.toLowerCase());
    //     }

    //     return optionalUser.orElseThrow(() -> new RuntimeException("User not found."));
    // }

    private User findUserByIdentifier(String identifier) {
        System.out.println("=== SEARCHING FOR USER ===");
        System.out.println("Identifier: " + identifier);
        
        Optional<User> optionalUser = Optional.empty();

        if (identifier.matches("^(?i)[dptna]\\d+$")) { 
            System.out.println("Searching by User ID pattern");
            optionalUser = repo.findByUserIdIgnoreCase(identifier);
        } else if (identifier.matches("^\\d{11}$") || identifier.matches("^\\+8801\\d{9}$")) {
            System.out.println("Searching by phone pattern");
            if (identifier.startsWith("01")) {
                identifier = "+88" + identifier;
            }
            optionalUser = repo.findByPhone(identifier);
        } else if (identifier.contains("@")) {
            System.out.println("Searching by email pattern");
            optionalUser = repo.findByEmailIgnoreCase(identifier.toLowerCase());
        } else {
            System.out.println("No pattern matched for identifier: " + identifier);
        }

        System.out.println("User found: " + optionalUser.isPresent());
        return optionalUser.orElseThrow(() -> new RuntimeException("User not found."));
    }

    // Helper method to get user profile data
    // private Map<String, Object> getUserProfileData(User user) {
    //     Map<String, Object> profileData = new HashMap<>();
        
    //     switch (user.getRole()) {
    //         case DOCTOR:
    //             Optional<Doctor> doctor = doctorRepo.findById(user.getUserId());
    //             if (doctor.isPresent()) {
    //                 Doctor d = doctor.get();
    //                 profileData.put("firstName", d.getFirstName());
    //                 profileData.put("lastName", d.getLastName());
    //                 profileData.put("name", d.getFirstName() + " " + d.getLastName());
    //                 profileData.put("specialization", d.getSpecialization());
    //                 profileData.put("degree", d.getDegree());
    //             }
    //             break;
    //         case PATIENT:
    //             Optional<Patient> patient = patientRepo.findById(user.getUserId());
    //             if (patient.isPresent()) {
    //                 Patient p = patient.get();
    //                 profileData.put("firstName", p.getFirstName());
    //                 profileData.put("lastName", p.getLastName());
    //                 profileData.put("name", p.getFirstName() + " " + p.getLastName());
    //                 profileData.put("age", p.getAge());
    //                 profileData.put("gender", p.getGender());
    //                 profileData.put("bloodGroup", p.getBloodGroup());
    //             }
    //             break;
    //         case TECHNICIAN:
    //             Optional<Technician> technician = technicianRepo.findById(user.getUserId());
    //             if (technician.isPresent()) {
    //                 Technician t = technician.get();
    //                 profileData.put("firstName", t.getFirstName());
    //                 profileData.put("lastName", t.getLastName());
    //                 profileData.put("name", t.getFirstName() + " " + t.getLastName());
    //                 profileData.put("specialization", t.getSpecialization());
    //             }
    //             break;
    //     }
        
    //     return profileData;
    // }


    private Map<String, Object> getUserProfileData(User user) {
    Map<String, Object> profileData = new HashMap<>();
    
    System.out.println("=== GETTING PROFILE DATA ===");
    System.out.println("User ID: " + user.getUserId());
    System.out.println("User Role: " + user.getRole());
    
    switch (user.getRole()) {
        case DOCTOR:
            System.out.println("Fetching doctor profile for ID: " + user.getUserId());
            System.out.println("Type of user.getUserId(): " + user.getUserId().getClass().getName());

            System.out.println("SURUUUUU");
            try {
                System.out.println("ENTER korsi");
                if(user.getUserId().equals("D202506003")){System.out.println("Done");}
                System.out.println("Type of user.getUserId(): " + user.getUserId().getClass().getName());

                Optional<Doctor> doctor = doctorRepo.findById(user.getUserId());
                System.out.println("Doctor found: " + doctor.isPresent());
                if (doctor.isPresent()) {
                    Doctor d = doctor.get();
                    profileData.put("firstName", d.getFirstName());
                    profileData.put("lastName", d.getLastName());
                    profileData.put("name", d.getFirstName() + " " + d.getLastName());
                    profileData.put("specialization", d.getSpecialization());
                    profileData.put("degree", d.getDegree());
                    System.out.println("Doctor profile loaded successfully");
                }
            } catch (Exception e) {
                System.err.println("Error fetching doctor profile: " + e.getMessage());
                e.printStackTrace();
            }
            break;
        case PATIENT:
            System.out.println("Fetching patient profile for ID: " + user.getUserId());
            System.out.println();
            try {
                Optional<Patient> patient = patientRepo.findById(user.getUserId());
                System.out.println("Patient found: " + patient.isPresent());
                if (patient.isPresent()) {
                    Patient p = patient.get();
                    profileData.put("firstName", p.getFirstName());
                    profileData.put("lastName", p.getLastName());
                    profileData.put("name", p.getFirstName() + " " + p.getLastName());
                    profileData.put("age", p.getAge());
                    profileData.put("gender", p.getGender());
                    profileData.put("bloodGroup", p.getBloodGroup());
                    System.out.println("Patient profile loaded successfully");
                }
            } catch (Exception e) {
                System.err.println("Error fetching patient profile: " + e.getMessage());
                e.printStackTrace();
            }
            break;
        case TECHNICIAN:
            System.out.println("Fetching technician profile for ID: " + user.getUserId());
            try {
                Optional<Technician> technician = technicianRepo.findById(user.getUserId());
                System.out.println("Technician found: " + technician.isPresent());
                if (technician.isPresent()) {
                    Technician t = technician.get();
                    profileData.put("firstName", t.getFirstName());
                    profileData.put("lastName", t.getLastName());
                    profileData.put("name", t.getFirstName() + " " + t.getLastName());
                    profileData.put("specialization", t.getSpecialization());
                    System.out.println("Technician profile loaded successfully");
                }
            } catch (Exception e) {
                System.err.println("Error fetching technician profile: " + e.getMessage());
                e.printStackTrace();
            }
            break;
    }
    
    System.out.println("Profile data keys: " + profileData.keySet());
    return profileData;
}

    // CORRECTED: Login method now returns complete user data and handles status properly
    @Override
    public Map<String, Object> login(LoginRequest req) {
        String identifier = req.getIdentifier().trim();
        String rawPassword = req.getPassword();

        User user;
        try {
            user = findUserByIdentifier(identifier);
        } catch (RuntimeException e) {
            System.out.println("=== USER NOT FOUND ===");
            throw new RuntimeException("Invalid credentials.");
        }

        System.out.println("=== PASSWORD CHECK ===");
        System.out.println("Raw password length: " + rawPassword.length());
        System.out.println("Encoded password: " + user.getPassword());
        System.out.println("Password matches: " + encoder.matches(rawPassword, user.getPassword()));
        // Check password first
        if (!encoder.matches(rawPassword, user.getPassword())) {
            System.out.println("=== PASSWORD CHECK FAILED ===");
            throw new RuntimeException("Invalid credentials.");
        }

        // Check user status
        if (user.getStatus() == UserStatus.PENDING) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "PENDING");
            response.put("message", "Your account is pending approval.");
            return response;
        }

        if (user.getStatus() == UserStatus.REJECTED) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "REJECTED");
            response.put("message", "Your account has been rejected. Please contact support.");
            return response;
        }

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not active.");
        }

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())
                .password(user.getPassword())
                .authorities("ROLE_" + user.getRole().name())
                .build();

        String token = jwtUtil.generateToken(userDetails);

        // Get profile data
        //Map<String, Object> profileData = getUserProfileData(user);

        // Build complete response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("role", user.getRole().name().toLowerCase());
        response.put("status", user.getStatus().name());
        
        // Add profile data
        //response.putAll(profileData);

        return response;
    }

    // CORRECTED: Google login method with proper status handling
    public Map<String, Object> loginWithGoogleAsPatient(String idToken) {
        FirebaseToken decoded;
        try {
            decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Invalid Google token");
        }

        String email = decoded.getEmail();
        String fullName = decoded.getName();
        String firebaseUid = decoded.getUid();

        if (email == null) {
            throw new IllegalArgumentException("Google account has no email.");
        }

        Optional<User> optUser = repo.findByEmailIgnoreCase(email.trim().toLowerCase());
        User user;

        if (optUser.isPresent()) {
            user = optUser.get();

            // Check if user is not a patient
            if (user.getRole() != UserRole.PATIENT) {
                throw new IllegalArgumentException("Google sign-in is only allowed for patients.");
            }

            // Handle non-active status
            if (user.getStatus() == UserStatus.PENDING) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "PENDING");
                response.put("message", "Your account is pending approval.");
                return response;
            }

            if (user.getStatus() == UserStatus.REJECTED) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "REJECTED");
                response.put("message", "Your account has been rejected. Please contact support.");
                return response;
            }

        } else {
            // Create new patient user
            RegisterRequest fake = new RegisterRequest();
            fake.setEmail(email.trim().toLowerCase());
            fake.setPhone(null);
            fake.setPassword("GOOGLE_USER"); // Placeholder password

            String userId = createAndSaveUserOnly(fake, UserRole.PATIENT, UserStatus.ACTIVE, "P");

            // Create patient profile
            PatientRegistrationDto pr = new PatientRegistrationDto();
            String[] nameParts = fullName != null ? fullName.split(" ") : new String[]{"User"};
            pr.setFirstName(nameParts[0]);
            pr.setLastName(nameParts.length > 1 ? nameParts[1] : "");
            pr.setAge(0);
            pr.setGender(null);
            pr.setBloodGroup(null);
            createPatientProfile(userId, pr);

            user = repo.findByUserIdIgnoreCase(userId)
                      .orElseThrow(() -> new IllegalStateException("Just created patient not found."));
        }

        // Generate JWT token
        UserDetails userDetails = org.springframework.security.core.userdetails.User
            .withUsername(user.getUserId())
            .password(user.getPassword())
            .authorities("ROLE_" + user.getRole().name())
            .build();

        String token = jwtUtil.generateToken(userDetails);

        // Get profile data
        Map<String, Object> profileData = getUserProfileData(user);

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getUserId());
        response.put("email", user.getEmail());
        response.put("phone", user.getPhone());
        response.put("role", user.getRole().name().toLowerCase());
        response.put("status", user.getStatus().name());
        
        // Add profile data
        response.putAll(profileData);

        return response;

    }
}
