package com.pulseiq.service;

import com.pulseiq.dto.*;

import java.util.Map;

public interface UserService {
//    void register(RegisterRequest request);
    Map<String, String> login(LoginRequest request);
    void registerDoctor(DoctorRegistrationDto dto);
    void registerPatient(PatientRegistrationDto dto);
    void registerTechnician(TechnicianRegistrationDto dto);


}
