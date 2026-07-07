package com.schoolsystem.sms.controller;

import com.schoolsystem.sms.model.Teacher;
import com.schoolsystem.sms.repository.TeacherRepository;
import com.schoolsystem.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final UserService userService;

    @GetMapping("/teachers")
    public String viewTeachers(Model model, Authentication authentication) {
        addCommonAttributes(model, authentication);
        model.addAttribute("teachers", teacherRepository.findAll());

        if (!model.containsAttribute("teacher")) {
            model.addAttribute("teacher", new Teacher());
        }

        return "teachers";
    }

    @PostMapping("/register-teacher")
    public String registerTeacher(@ModelAttribute("teacher") Teacher teacher,
                                  Model model, Authentication authentication) {
        try {
            teacherRepository.save(teacher);
            return "redirect:/teachers?success";
        } catch (Exception e) {
            addCommonAttributes(model, authentication);
            model.addAttribute("errorMessage", "Error: Employee ID already exists or is invalid.");
            return viewTeachers(model, authentication);
        }
    }

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