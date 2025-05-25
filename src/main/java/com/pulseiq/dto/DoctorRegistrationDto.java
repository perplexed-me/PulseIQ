package com.pulseiq.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
@Data
public class DoctorRegistrationDto extends RegisterRequest {
    @NotBlank private String specialization;
    @NotBlank private String degree;
    @NotBlank private String licenseNumber;
    private String assistantName;
    private String assistantNumber;
    private String consultationFee;
}

//@Data
//public class DoctorRegistrationDto {
//    @NotBlank(message = "Doctor ID is required")
//    private String doctorId;
//
//    @NotNull(message = "Hospital ID is required")
//    private Integer hospitalId;
//
//    @NotBlank(message = "Password is required")
//    @Size(min = 6, message = "Password must be at least 6 characters")
//    private String password;
//
//    @Email(message = "Invalid email format")
//    @NotBlank(message = "Email is required")
//    private String email;
//
//    @Pattern(regexp = "01\\d{9}", message = "Phone must be 11 digits starting with 01")
//    @NotBlank(message = "Phone is required")
//    private String phone;
//
//    @NotBlank(message = "First name is required")
//    @Size(max = 50)
//    private String firstName;
//
//    @NotBlank(message = "Last name is required")
//    @Size(max = 50)
//    private String lastName;
//
//    @NotBlank(message = "Specialization is required")
//    @Size(max = 100)
//    private String specialization;
//
//    @NotBlank(message = "Degree is required")
//    @Size(max = 100)
//    private String degree;
//
//    @NotBlank(message = "License number is required")
//    @Size(max = 50)
//    private String licenseNumber;
//
//    @Size(max = 50)
//    private String assistantName;
//
//    @Pattern(regexp = "01\\d{9}", message = "Assistant number must be 11 digits starting with 01")
//    private String assistantNumber;
//
//    @DecimalMin(value = "0.0", message = "Consultation fee must be non-negative")
//    private BigDecimal consultationFee = BigDecimal.ZERO;
//}