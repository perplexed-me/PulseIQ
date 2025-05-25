package com.pulseiq.dto;

import lombok.Data;
import jakarta.validation.constraints.*;
import com.pulseiq.entity.Patient;



@Data
public class PatientRegistrationDto extends RegisterRequest {

    @NotNull(message = "Age is required")
    @Min(value = 1, message = "Age must be at least 1")
    @Max(value = 109, message = "Age must be realistic")
    private Integer age;

    @NotNull(message = "Gender is required")
    private Patient.Gender gender;

    private Patient.BloodGroup bloodGroup;
}

//@Data
//public class PatientRegistrationDto {
//    @NotBlank(message = "Patient ID is required")
//    private String patientId;
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
//    @Min(value = 1, message = "Age must be greater than 0")
//    @Max(value = 109, message = "Age must be less than 110")
//    @NotNull(message = "Age is required")
//    private Integer age;
//
//    @NotNull(message = "Gender is required")
//    private Patient.Gender gender;
//
//    private Patient.BloodGroup bloodGroup;
//}
