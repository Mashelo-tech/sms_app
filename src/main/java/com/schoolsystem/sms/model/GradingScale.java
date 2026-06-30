package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "grading_scales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int minMark; // Inclusive
    private int maxMark; // Inclusive

    @Column(nullable = false)
    private String grade; // e.g. "D1", "A"

    private int points; // e.g. 1, 6 (for aggregation)

    private String comment; // e.g. "Distinction", "Fail"
}
