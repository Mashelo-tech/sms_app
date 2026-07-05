package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;

    @Override
    public Page<Student> getPaginatedStudents(int pageNo, int pageSize) {
        return studentRepository.findAll(PageRequest.of(pageNo - 1, pageSize));
    }

    @Override
    public void saveStudent(Student student) {
        if (existsByRegistrationNumber(student.getRegistrationNumber())) {
            throw new IllegalArgumentException("Student with this Registration Number already exists.");
        }
        studentRepository.save(student);
    }

    @Override
    public boolean existsByRegistrationNumber(String regNo) {
        return studentRepository.existsByRegistrationNumber(regNo); // Assuming RegNo is the @Id
    }
}