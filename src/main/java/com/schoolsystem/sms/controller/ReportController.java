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

    // ─── INDIVIDUAL STUDENT REPORT ────────────────────────────────────────────
    @GetMapping("/student")
    public String viewStudentReport(
            @RequestParam Long studentId,
            @RequestParam Long termId,
            Model model,
            Authentication authentication) {

        addCommonAttributes(model, authentication);
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("students", studentRepository.findAll());
        model.addAttribute("selectedTermId", termId);
        model.addAttribute("selectedStudentId", studentId);

        studentRepository.findById(studentId).ifPresent(s -> model.addAttribute("selectedStudent", s));
        termRepository.findById(termId).ifPresent(t -> model.addAttribute("selectedTerm", t));

        try {
            StudentReportDTO report = reportService.generateStudentReport(studentId, termId);
            model.addAttribute("report", report);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Could not generate report: " + e.getMessage());
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
