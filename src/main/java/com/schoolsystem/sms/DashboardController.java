package com.schoolsystem.sms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.schoolsystem.sms.model.Student; // Use your actual Student entity package

// Replace these imports with the exact package names of your Repositories
import com.schoolsystem.sms.repository.StudentRepository; 

@Controller
public class DashboardController {

    @Autowired
    private StudentRepository studentRepository; // Spring injects your JPA interface here

    @GetMapping("/")
    public String viewDashboard(Model model) {
        // This pulls all students from your H2 database and hands them to Thymeleaf
        model.addAttribute("students", studentRepository.findAll()); 
        return "index"; 
    }

    @PostMapping("/register-student")
public String saveStudent(@ModelAttribute Student student) {
    studentRepository.save(student); // Saves the submitted form data to H2
    return "redirect:/"; // Reloads the dashboard to display the updated table
}
}