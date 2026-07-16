package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String primaryPhoneNumber;

    private String nationalIdNumber;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Student> dependents;
}
