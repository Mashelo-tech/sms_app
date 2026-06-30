package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "academic_years")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "2024"

    private LocalDate startDate;
    private LocalDate endDate;

    private boolean active; // Current academic year

    @OneToMany(mappedBy = "academicYear", cascade = CascadeType.ALL)
    private List<Term> terms;
}
