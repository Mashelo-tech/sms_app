package com.schoolsystem.sms;

import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.model.User;
import com.schoolsystem.sms.repository.*;
import com.schoolsystem.sms.service.StudentService;
import com.schoolsystem.sms.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public String viewTeacherDashboard(Model model, Authentication authentication) {
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

        return "dashboard-teacher";
    }

    // ─── SECRETARY DASHBOARD ─────────────────────────────────────────────────
    @GetMapping("/dashboard/secretary")
    public String viewSecretaryDashboard(Model model, Authentication authentication,
                                         @RequestParam(defaultValue = "1") int page) {
        addCommonAttributes(model, authentication);

        int pageSize = 20;
        Page<Student> studentPage = studentService.getPaginatedStudents(page, pageSize);

        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        model.addAttribute("totalStudents", studentPage.getTotalElements());
        model.addAttribute("classLevels", classLevelRepository.findAll());

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