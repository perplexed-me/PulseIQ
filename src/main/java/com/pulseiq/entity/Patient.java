package com.pulseiq.entity;

import com.pulseiq.converter.BloodGroupConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "patients", schema = "PulseIQ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {

    @Id
    @Column(name = "patient_id")
    private String patientId;

    // 🔗 Reference to User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", referencedColumnName = "userId", insertable = false, updatable = false)
    private User user;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Min(1)
    @Max(109)
    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Convert(converter = BloodGroupConverter.class)
    @Column(name = "blood_group", length = 5)
    private BloodGroup bloodGroup;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate = LocalDateTime.now();

    public enum Gender {
        Male, Female, Other
    }

    public enum BloodGroup {
        A_POSITIVE("A+"), A_NEGATIVE("A-"),
        B_POSITIVE("B+"), B_NEGATIVE("B-"),
        AB_POSITIVE("AB+"), AB_NEGATIVE("AB-"),
        O_POSITIVE("O+"), O_NEGATIVE("O-");

        private final String value;
        BloodGroup(String value) { this.value = value; }
        public String getValue() { return value; }
    }
}
