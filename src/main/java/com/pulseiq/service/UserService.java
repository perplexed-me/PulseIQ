package com.pulseiq.service;

import com.google.firebase.auth.FirebaseAuthException;
import com.pulseiq.dto.*;

import java.util.Map;

public interface UserService {
//    void register(RegisterRequest request);
    Map<String, String> login(LoginRequest request);
    Map<String, String> loginWithGoogleAsPatient(String id) throws FirebaseAuthException;
    void registerDoctor(DoctorRegistrationDto dto);
    void registerPatient(PatientRegistrationDto dto);
    void registerTechnician(TechnicianRegistrationDto dto);


}
