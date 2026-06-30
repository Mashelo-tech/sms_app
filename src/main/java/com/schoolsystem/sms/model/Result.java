package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "results", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_id", "term_id", "academic_year_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_level_id", nullable = false)
    private ClassLevel classLevel; // The class level at the time of exam

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "term_id", nullable = false)
    private Term term;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id", nullable = false)
    private AcademicYear academicYear;

    private double marks; // Raw marks (e.g. 85.5)

    private String grade; // e.g. "D1" (Calculated)
    private int points;   // e.g. 1 (Calculated)

    private String remark; // e.g. "Excellent"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResultStatus status = ResultStatus.DRAFT;
    
    // Audit fields
    private String lastModifiedBy;
}
