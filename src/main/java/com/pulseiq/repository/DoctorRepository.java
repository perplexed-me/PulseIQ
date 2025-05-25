package com.pulseiq.repository;

import com.pulseiq.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, String> {
    boolean existsByLicenseNumber(String licenseNumber);
    boolean existsByDoctorId(String doctorId);
    boolean existsByAssistantNumber(String assistantNumber);
}