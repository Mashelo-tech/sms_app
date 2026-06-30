package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.TeacherAssignment;
import com.schoolsystem.sms.model.User;
import com.schoolsystem.sms.model.ClassLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TeacherAssignmentRepository extends JpaRepository<TeacherAssignment, Long> {
    List<TeacherAssignment> findByTeacher(User teacher);
    List<TeacherAssignment> findByClassLevel(ClassLevel classLevel);
}
