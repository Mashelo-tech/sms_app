package com.schoolsystem.sms.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @NotBlank(message = "Registration Number cannot be empty")
    private String registrationNumber;

    @NotBlank(message = "Full Name is required")
    private String fullName;

    @NotBlank(message = "Gender must be specified")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Invalid gender input")
    private String gender;

    private LocalDate dateOfBirth;
    
    private LocalDate enrollmentDate;

    // The class the student is CURRENTLY in (e.g. "Primary 1")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_class_id")
    private ClassLevel currentClass;
}
