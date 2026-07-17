package com.schoolsystem.sms;

import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.model.User;
import com.schoolsystem.sms.repository.*;
import com.schoolsystem.sms.service.StudentService;
import com.schoolsystem.sms.service.UserService;
import com.schoolsystem.sms.service.AttendanceService;
import com.schoolsystem.sms.service.FinanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService studentService;
    private final UserService userService;
    private final TeacherAssignmentRepository teacherAssignmentRepository;
    private final ClassLevelRepository classLevelRepository;
    private final AcademicYearRepository academicYearRepository;
    private final TermRepository termRepository;
    private final SubjectRepository subjectRepository;
    private final StudentRepository studentRepository;
    private final AttendanceService attendanceService;
    private final FinanceService financeService;
    private final InvoiceRepository invoiceRepository;

    // ─── MAIN ADMIN/DOS/HEADTEACHER DASHBOARD ───────────────────────────────
    @GetMapping("/")
    public String viewDashboard(Model model, Authentication authentication,
                                @RequestParam(defaultValue = "1") int page) {
        addCommonAttributes(model, authentication);

        int pageSize = 20;
        Page<Student> studentPage = studentService.getPaginatedStudents(page, pageSize);

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalStudents", studentPage.getTotalElements());
        model.addAttribute("classLevels", classLevelRepository.findAll());
        model.addAttribute("activeYear", academicYearRepository.findByActiveTrue().orElse(null));
        model.addAttribute("activeTerm", termRepository.findByActiveTrue().orElse(null));

        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }

        return "index";
    }

    // ─── TEACHER DASHBOARD ───────────────────────────────────────────────────
    @GetMapping("/dashboard/teacher")
    public String viewTeacherDashboard(@RequestParam(required = false) Long classId,
                                       @RequestParam(required = false) String date,
                                       @RequestParam(required = false) String error,
                                       @RequestParam(required = false) String success,
                                       Model model, Authentication authentication) {
        addCommonAttributes(model, authentication);

        // Get the logged-in user's assignments
        Optional<User> currentUser = userService.findByUsername(authentication.getName());
        currentUser.ifPresent(user -> {
            model.addAttribute("assignments", teacherAssignmentRepository.findByTeacher(user));
        });

        model.addAttribute("classLevels", classLevelRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("activeTerm", termRepository.findByActiveTrue().orElse(null));
        model.addAttribute("activeYear", academicYearRepository.findByActiveTrue().orElse(null));

        LocalDate targetDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();
        model.addAttribute("currentDate", targetDate);

        if (error != null) {
            model.addAttribute("errorMessage", error);
        }
        if (success != null) {
            model.addAttribute("success", true);
        }

        if (classId != null && currentUser.isPresent()) {
            classLevelRepository.findById(classId).ifPresent(c -> {
                model.addAttribute("selectedClass", c);
                model.addAttribute("students", studentRepository.findByCurrentClass(c));

                UUID tenantId = currentUser.get().getTenantId();
                if (tenantId != null) {
                    model.addAttribute("attendanceMap", attendanceService.getAttendanceStatuses(tenantId, targetDate, classId));
                }
            });
        }

        return "dashboard-teacher";
    }

    // ─── SECRETARY DASHBOARD ─────────────────────────────────────────────────
    @GetMapping("/dashboard/secretary")
    public String viewSecretaryDashboard(@RequestParam(required = false) String success,
                                         @RequestParam(required = false) String paymentSuccess,
                                         @RequestParam(required = false) String error,
                                         Model model, Authentication authentication,
                                         @RequestParam(defaultValue = "1") int page) {
        addCommonAttributes(model, authentication);

        int pageSize = 20;
        Page<Student> studentPage = studentService.getPaginatedStudents(page, pageSize);

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalStudents", studentPage.getTotalElements());
        model.addAttribute("classLevels", classLevelRepository.findAll());

        Optional<User> currentUser = userService.findByUsername(authentication.getName());
        if (currentUser.isPresent() && currentUser.get().getTenantId() != null) {
            UUID tenantId = currentUser.get().getTenantId();
            java.util.Map<Long, java.math.BigDecimal> balancesMap = new java.util.HashMap<>();
            java.util.Map<Long, java.util.List<com.schoolsystem.sms.model.Invoice>> openInvoicesMap = new java.util.HashMap<>();

            for (Student student : studentPage.getContent()) {
                balancesMap.put(student.getId(), financeService.getOutstandingBalance(tenantId, student.getId()));

                java.util.List<com.schoolsystem.sms.model.Invoice> invoices = invoiceRepository.findByTenantIdAndStudentId(tenantId, student.getId());
                openInvoicesMap.put(student.getId(), invoices.stream().filter(inv -> inv.getTotalAmountIssued().compareTo(inv.getTotalAmountPaid()) > 0).toList());
            }

            model.addAttribute("balancesMap", balancesMap);
            model.addAttribute("openInvoicesMap", openInvoicesMap);
        }

        if (success != null) {
            // Keep original success functionality for registration
            model.addAttribute("success", true);
        }
        if (paymentSuccess != null) {
            model.addAttribute("successMessage", paymentSuccess);
        }
        if (error != null) {
            model.addAttribute("errorMessage", error);
        }

        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }

        return "dashboard-secretary";
    }

    // ─── STUDENT REGISTRATION ─────────────────────────────────────────────────
    @PostMapping("/register-student")
    public String registerStudent(@Valid @ModelAttribute("student") Student student,
                                  BindingResult result,
                                  Model model,
                                  Authentication authentication) {
        if (result.hasErrors()) {
            return viewDashboard(model, authentication, 1);
        }

        try {
            studentService.saveStudent(student);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return viewDashboard(model, authentication, 1);
        }

        // Redirect based on role
        String role = authentication.getAuthorities().iterator().next().getAuthority();
        return switch (role) {
            case "ROLE_SECRETARY" -> "redirect:/dashboard/secretary?success";
            default               -> "redirect:/?success";
        };
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