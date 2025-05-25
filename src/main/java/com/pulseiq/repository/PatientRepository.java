package com.pulseiq.repository;

import com.pulseiq.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {
    boolean existsByPatientId(String patientId);
}