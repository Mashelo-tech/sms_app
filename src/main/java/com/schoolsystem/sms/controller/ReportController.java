package com.schoolsystem.sms.controller;

import com.schoolsystem.sms.dto.StudentReportDTO;
import com.schoolsystem.sms.repository.ClassLevelRepository;
import com.schoolsystem.sms.repository.StudentRepository;
import com.schoolsystem.sms.repository.TermRepository;
import com.schoolsystem.sms.service.ReportService;
import com.schoolsystem.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ClassLevelRepository classLevelRepository;
    private final TermRepository termRepository;
    private final StudentRepository studentRepository;
    private final UserService userService;

    // ─── REPORTS HOME ─────────────────────────────────────────────────────────
    @GetMapping
    public String showReportsPage(Model model, Authentication authentication) {
        addCommonAttributes(model, authentication);
        model.addAttribute("classLevels", classLevelRepository.findAll());
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("activeTerm", termRepository.findByActiveTrue().orElse(null));
        return "reports";
    }

    // ─── CLASS BROADSHEET ─────────────────────────────────────────────────────
    @GetMapping("/broadsheet")
    public String viewBroadsheet(
            @RequestParam Long classId,
            @RequestParam Long termId,
            Model model,
            Authentication authentication) {

        addCommonAttributes(model, authentication);
        model.addAttribute("classLevels", classLevelRepository.findAll());
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("selectedClassId", classId);
        model.addAttribute("selectedTermId", termId);

        classLevelRepository.findById(classId).ifPresent(c -> model.addAttribute("selectedClass", c));
        termRepository.findById(termId).ifPresent(t -> model.addAttribute("selectedTerm", t));

        try {
            List<StudentReportDTO> broadsheet = reportService.generateClassBroadsheet(classId, termId);
            model.addAttribute("broadsheet", broadsheet);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not generate broadsheet: " + e.getMessage());
        }

        return "reports-broadsheet";
    }

    // ─── STUDENT REPORTS (INDIVIDUAL / BULK) ───────────────────────────────────
    @GetMapping("/student")
    public String viewStudentReport(
            @RequestParam(required = false, defaultValue = "INDIVIDUAL") String scope,
            @RequestParam(required = false) String registrationNumber,
            @RequestParam(required = false) Long classId,
            @RequestParam Long termId,
            Model model,
            Authentication authentication) {

        addCommonAttributes(model, authentication);
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("selectedTermId", termId);
        termRepository.findById(termId).ifPresent(t -> model.addAttribute("selectedTerm", t));

        java.util.List<StudentReportDTO> reports = new java.util.ArrayList<>();

        try {
            if ("INDIVIDUAL".equalsIgnoreCase(scope)) {
                if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                    throw new IllegalArgumentException("Registration Number is required for individual reports.");
                }
                var studentOpt = studentRepository.findByRegistrationNumber(registrationNumber.trim());
                if (studentOpt.isEmpty()) {
                    throw new IllegalArgumentException("No student found with Registration Number: " + registrationNumber);
                }
                reports.add(reportService.generateStudentReport(studentOpt.get().getId(), termId));

            } else if ("CLASS".equalsIgnoreCase(scope)) {
                if (classId == null) {
                    throw new IllegalArgumentException("Class selection is required for class-level reports.");
                }
                var classOpt = classLevelRepository.findById(classId);
                if (classOpt.isEmpty()) {
                    throw new IllegalArgumentException("Selected class not found.");
                }
                var students = studentRepository.findByCurrentClass(classOpt.get());
                for (var student : students) {
                    try {
                        reports.add(reportService.generateStudentReport(student.getId(), termId));
                    } catch (Exception ignored) { } // Skip students who can't have a report generated
                }

            } else if ("SCHOOL".equalsIgnoreCase(scope)) {
                var allStudents = studentRepository.findAll();
                for (var student : allStudents) {
                    try {
                        reports.add(reportService.generateStudentReport(student.getId(), termId));
                    } catch (Exception ignored) { }
                }
            } else {
                throw new IllegalArgumentException("Invalid scope provided.");
            }

            model.addAttribute("reports", reports);

        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not generate report(s): " + e.getMessage());
        }

        return "reports-student";
    }

    // ─── HELPER ──────────────────────────────────────────────────────────────
    private void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null) {
            model.addAttribute("currentUsername", authentication.getName());
            String role = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("ROLE_UNKNOWN")
                    .replace("ROLE_", "");
            model.addAttribute("currentRole", role);
            userService.findByUsername(authentication.getName())
                    .ifPresent(u -> model.addAttribute("currentUser", u));
        }
    }
}
