package com.schoolsystem.sms;

import com.schoolsystem.sms.model.Role;
import com.schoolsystem.sms.model.User;
import com.schoolsystem.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @PreAuthorize("hasAnyRole('SUPER_DOS','DOS','HEADTEACHER')")
    public String viewSystemUsers(Model model, Authentication authentication) {
        addCommonAttributes(model, authentication);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", Role.values());
        return "users";
    }

    @PostMapping("/register-user")
    @PreAuthorize("hasAnyRole('SUPER_DOS','DOS')")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String fullName,
            @RequestParam String email,
            @RequestParam Role role,
            RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(username, password, fullName, email, role);
            redirectAttributes.addFlashAttribute("successMessage", "User '" + username + "' created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/users/{id}/toggle")
    @PreAuthorize("hasRole('SUPER_DOS')")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Future: enable/disable a user account
        redirectAttributes.addFlashAttribute("errorMessage", "Toggle feature coming soon.");
        return "redirect:/users";
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