package com.schoolsystem.sms.service;

import com.schoolsystem.sms.model.*;
import com.schoolsystem.sms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResultServiceImpl implements ResultService {

    private final ResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TermRepository termRepository;
    private final GradingScaleRepository gradingScaleRepository;
    private final AcademicYearRepository academicYearRepository;
    private final UserRepository userRepository;

    public ResultServiceImpl(ResultRepository resultRepository, StudentRepository studentRepository,
                             SubjectRepository subjectRepository, TermRepository termRepository,
                             GradingScaleRepository gradingScaleRepository, AcademicYearRepository academicYearRepository,
                             UserRepository userRepository) {
        this.resultRepository = resultRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.termRepository = termRepository;
        this.gradingScaleRepository = gradingScaleRepository;
        this.academicYearRepository = academicYearRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Result saveResult(Long studentId, Long subjectId, Long termId, double marks, String username) {
        if (marks < 0 || marks > 100) {
            throw new IllegalArgumentException("Marks must be between 0 and 100");
        }

        Student student = studentRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new RuntimeException("Subject not found"));
        Term term = termRepository.findById(termId).orElseThrow(() -> new RuntimeException("Term not found"));
        
        // Find existing result or create new
        // Note: For simplicity assuming 1 result per student/subject/term. 
        // In real app, we might check if exists. 
        // Here we just creating a new one or assuming ID is passed if update (refactoring needed for update logic)
        // Let's implement Find-Or-Create logic based on Unique Constraint
        
        Result result = resultRepository.findByStudentAndTerm(student, term).stream()
                .filter(r -> r.getSubject().getId().equals(subjectId))
                .findFirst()
                .orElse(Result.builder()
                        .student(student)
                        .subject(subject)
                        .term(term)
                        .classLevel(student.getCurrentClass())
                        .academicYear(term.getAcademicYear())
                        .status(ResultStatus.DRAFT)
                        .build());

        // specific workflow check
        if (result.getStatus() == ResultStatus.APPROVED || result.getStatus() == ResultStatus.LOCKED) {
            throw new RuntimeException("Cannot edit approved or locked results");
        }

        result.setMarks(marks);
        calculateGradeAndPoints(result);
        result.setLastModifiedBy(username);
        
        return resultRepository.save(result);
    }

    @Override
    public void calculateGradeAndPoints(Result result) {
        // cast to int for range check (or change repo to double)
        int markInt = (int) Math.round(result.getMarks());
        gradingScaleRepository.findByMark(markInt).ifPresentOrElse(scale -> {
            result.setGrade(scale.getGrade());
            result.setPoints(scale.getPoints());
            result.setRemark(scale.getComment());
        }, () -> {
            // Default if no scale found (shouldn't happen if setup correct)
            result.setGrade("X"); 
            result.setPoints(0);
        });
    }

    @Override
    @Transactional
    public List<Result> submitResults(List<Long> resultIds, String username) {
        List<Result> results = resultRepository.findAllById(resultIds);
        results.forEach(r -> {
            if (r.getStatus() == ResultStatus.DRAFT || r.getStatus() == ResultStatus.RETURNED) {
                r.setStatus(ResultStatus.SUBMITTED);
                r.setLastModifiedBy(username);
            }
        });
        return resultRepository.saveAll(results);
    }

    @Override
    @Transactional
    public List<Result> approveResults(List<Long> resultIds, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
        if (user.getRole() != Role.DOS && user.getRole() != Role.SUPER_DOS) {
             throw new RuntimeException("Only DOS can approve results");
        }

        List<Result> results = resultRepository.findAllById(resultIds);
        results.forEach(r -> {
            if (r.getStatus() == ResultStatus.SUBMITTED) {
                r.setStatus(ResultStatus.APPROVED);
                r.setLastModifiedBy(username);
            }
        });
        return resultRepository.saveAll(results);
    }

    @Override
    @Transactional
    public List<Result> returnResults(List<Long> resultIds, String username) {
        User user = userRepository.findByUsername(username).orElseThrow();
         if (user.getRole() != Role.DOS && user.getRole() != Role.SUPER_DOS) {
             throw new RuntimeException("Only DOS can return results");
        }

        List<Result> results = resultRepository.findAllById(resultIds);
        results.forEach(r -> {
            if (r.getStatus() == ResultStatus.SUBMITTED || r.getStatus() == ResultStatus.APPROVED) {
                r.setStatus(ResultStatus.RETURNED);
                r.setLastModifiedBy(username);
            }
        });
        return resultRepository.saveAll(results);
    }
}
