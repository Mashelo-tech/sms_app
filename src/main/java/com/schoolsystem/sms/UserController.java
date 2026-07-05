package com.schoolsystem.sms;

import com.schoolsystem.sms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/users")
    public String viewSystemUsers(Model model) {
        // Fetch all users from the database and send them to the HTML page
        model.addAttribute("users", userRepository.findAll());
        
        // Tells Spring to look for "users.html" in the templates folder
        return "users";
    }
}