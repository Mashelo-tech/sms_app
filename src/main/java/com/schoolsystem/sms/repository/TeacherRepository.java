package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    // Spring Data JPA automatically writes all the SQL for finding, saving, and deleting teachers!
}