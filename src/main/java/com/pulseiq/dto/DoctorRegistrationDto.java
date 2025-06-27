package com.pulseiq.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class DoctorRegistrationDto extends RegisterRequest {
    @NotBlank private String specialization;
    @NotBlank private String degree;
    @NotBlank private String licenseNumber;
    private String assistantName;
    private String assistantNumber;
    private String consultationFee;
}