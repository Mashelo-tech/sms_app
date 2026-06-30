package com.schoolsystem.sms.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "teacher_assignments", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"teacher_id", "class_level_id", "subject_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private User teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_level_id", nullable = false)
    private ClassLevel classLevel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;
}
