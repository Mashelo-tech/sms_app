package com.schoolsystem.sms.controller;

import com.schoolsystem.sms.model.Teacher;
import com.schoolsystem.sms.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;

    @GetMapping("/teachers")
    public String viewTeachers(Model model) {
        model.addAttribute("teachers", teacherRepository.findAll());
        
        // Give the modal an empty object to fill out
        if (!model.containsAttribute("teacher")) {
            model.addAttribute("teacher", new Teacher());
        }
        
        return "teachers";
    }

    @PostMapping("/register-teacher")
    public String registerTeacher(@ModelAttribute("teacher") Teacher teacher, Model model) {
        try {
            // Save to the MySQL database!
            teacherRepository.save(teacher);
            return "redirect:/teachers?success";
        } catch (Exception e) {
            // If the Employee ID already exists, MySQL will throw an error. Let's catch it nicely.
            model.addAttribute("errorMessage", "Error: Employee ID might already exist in the system.");
            return viewTeachers(model); 
        }
    }
}