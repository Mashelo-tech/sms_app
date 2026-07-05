package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.Student;
import org.springframework.data.domain.Page;

public interface StudentService {
    Page<Student> getPaginatedStudents(int pageNo, int pageSize);
    void saveStudent(Student student);
    boolean existsByRegistrationNumber(String regNo);
}