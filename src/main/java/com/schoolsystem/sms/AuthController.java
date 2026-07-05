package com.schoolsystem.sms;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String showLoginPage() {
        // This tells Spring to look for "login.html" in the templates folder
        return "login"; 
    }
}