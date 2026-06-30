package com.schoolsystem.sms.repository;

import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.model.ClassLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByRegNumber(String regNumber);
    List<Student> findByCurrentClass(ClassLevel currentClass);
}
