package com.schoolsystem.sms;

import com.schoolsystem.sms.model.Student;
import com.schoolsystem.sms.service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final StudentService studentService;

    // Load Dashboard with Pagination (Defaults to page 1, 20 items per page)
    @GetMapping("/")
    public String viewDashboard(Model model, @RequestParam(defaultValue = "1") int page) {
        int pageSize = 20;
        Page<Student> studentPage = studentService.getPaginatedStudents(page, pageSize);
        
        model.addAttribute("students", studentPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        
        // Add an empty student object for the registration form
        if (!model.containsAttribute("student")) {
            model.addAttribute("student", new Student());
        }
        
        return "index";
    }

    // Handle Student Registration with Validation
    @PostMapping("/register-student")
    public String registerStudent(@Valid @ModelAttribute("student") Student student, 
                                  BindingResult result, 
                                  Model model) {
        // If validation fails, return to the dashboard with error messages
        if (result.hasErrors()) {
            return viewDashboard(model, 1); 
        }

        try {
            studentService.saveStudent(student);
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return viewDashboard(model, 1);
        }

        return "redirect:/?success";
    }
}