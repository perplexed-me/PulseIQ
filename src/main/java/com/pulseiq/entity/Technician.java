package com.pulseiq.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "technicians", schema = "pulseiq")
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
