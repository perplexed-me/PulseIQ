package com.pulseiq.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "technicians", schema = "PulseIQ")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Technician {

    @Id
    @Column(name = "technician_id")
    private String technicianId;

    // 🔗 Link to User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id", referencedColumnName = "userId", insertable = false, updatable = false)
    private User user;


    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String specialization;
}
