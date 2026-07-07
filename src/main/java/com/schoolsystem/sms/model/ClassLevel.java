package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "class_levels")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClassLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // e.g. "Primary 1"

    private int levelOrder; // e.g. 1 for S1, 2 for S2 (for sorting)
}
