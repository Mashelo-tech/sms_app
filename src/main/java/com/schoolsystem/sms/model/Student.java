package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;

@Entity
@Table(name = "students")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String regNumber; // Unique Registration Number

    @Column(nullable = false)
    private String fullName;

    private String gender; // M/F

    private LocalDate dateOfBirth;
    
    private LocalDate enrollmentDate;

    // The class the student is CURRENTLY in (e.g. "Senior 1")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_class_id")
    private ClassLevel currentClass;
}
