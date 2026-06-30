package com.schoolsystem.sms.service;

import com.schoolsystem.sms.dto.StudentReportDTO;
import com.schoolsystem.sms.model.*;
import com.schoolsystem.sms.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    private final StudentRepository studentRepository;
    private final ResultRepository resultRepository;
    private final TermRepository termRepository;
    private final ClassLevelRepository classLevelRepository;

    public ReportServiceImpl(StudentRepository studentRepository, ResultRepository resultRepository,
                             TermRepository termRepository, ClassLevelRepository classLevelRepository) {
        this.studentRepository = studentRepository;
        this.resultRepository = resultRepository;
        this.termRepository = termRepository;
        this.classLevelRepository = classLevelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentReportDTO> generateClassBroadsheet(Long classLevelId, Long termId) {
        ClassLevel classLevel = classLevelRepository.findById(classLevelId)
                .orElseThrow(() -> new RuntimeException("Class not found"));
        Term term = termRepository.findById(termId)
                .orElseThrow(() -> new RuntimeException("Term not found"));

        List<Student> students = studentRepository.findByCurrentClass(classLevel);
        List<StudentReportDTO> reports = new ArrayList<>();

        for (Student student : students) {
            reports.add(generateStudentReportDTO(student, term));
        }

        // Calculate Ranks
        // Sort by Total Marks Descending
        reports.sort(Comparator.comparingDouble(StudentReportDTO::getTotalMarks).reversed());

        int rank = 1;
        for (int i = 0; i < reports.size(); i++) {
            StudentReportDTO report = reports.get(i);
            // Handle ties (simple implementation: skip ranks for ties or dense rank? doing simple 1,2,3 for now)
            // Ideally check if EQUAL to previous, then same rank.
            if (i > 0 && report.getTotalMarks() == reports.get(i - 1).getTotalMarks()) {
                report.setPosition(reports.get(i - 1).getPosition());
            } else {
                report.setPosition(i + 1);
            }
        }
        
        return reports;
    }

    @Override
    @Transactional(readOnly = true)
    public StudentReportDTO generateStudentReport(Long studentId, Long termId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Term term = termRepository.findById(termId).orElseThrow();
        
        // To get position, we unfortunately need the whole class stats 
        // Or we can query just the raw totals order by... using SQL.
        // For efficiency in large sets, we'd use a Custom Repository Query.
        // For simplicity here, I'll reuse the broadsheet logic (expensive but safe for small schools).
        
        List<StudentReportDTO> classReports = generateClassBroadsheet(student.getCurrentClass().getId(), termId);
        
        return classReports.stream()
                .filter(r -> r.getStudent().getId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Student report generation failed"));
    }

    private StudentReportDTO generateStudentReportDTO(Student student, Term term) {
        List<Result> results = resultRepository.findByStudentAndTerm(student, term);
        
        // Filter only APPROVED results for official reports? 
        // Usually reports show what is there, but DRAFT might be hidden. 
        // Let's assume we show everything for now, or filter in stream.
        
        double total = results.stream().mapToDouble(Result::getMarks).sum();
        double avg = results.isEmpty() ? 0 : total / results.size();
        
        return StudentReportDTO.builder()
                .student(student)
                .results(results) // Contains subject, grade, mark
                .totalMarks(total)
                .averageMarks(avg)
                .build();
    }
}
