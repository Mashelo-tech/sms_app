package com.schoolsystem.sms.controller;

import com.schoolsystem.sms.model.AttendanceStatus;
import com.schoolsystem.sms.model.Teacher;
import com.schoolsystem.sms.repository.ClassLevelRepository;
import com.schoolsystem.sms.repository.StudentRepository;
import com.schoolsystem.sms.repository.TeacherRepository;
import com.schoolsystem.sms.service.AttendanceService;
import com.schoolsystem.sms.service.UserService;
import com.schoolsystem.sms.util.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherRepository teacherRepository;
    private final UserService userService;
    private final StudentRepository studentRepository;
    private final AttendanceService attendanceService;
    private final ClassLevelRepository classLevelRepository;

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

    @GetMapping("/teacher/attendance")
    public String viewAttendanceRedirect(@RequestParam(required = false) Long classId,
                                         @RequestParam(required = false) String date) {
        String query = "";
        if (classId != null) {
            query += "?classId=" + classId;
        }

        String targetDate = (date != null && !date.isEmpty()) ? date : LocalDate.now().toString();
        query += (query.isEmpty() ? "?" : "&") + "date=" + targetDate;

        return "redirect:/dashboard/teacher" + query;
    }

    @PostMapping("/teacher/attendance/save")
    public String saveAttendance(@RequestParam Long classId,
                                 @RequestParam(required = false) String date,
                                 @RequestParam Map<String, String> allRequestParams,
                                 Authentication authentication) {

        UUID tenantId = userService.findByUsername(authentication.getName())
                .map(com.schoolsystem.sms.model.User::getTenantId)
                .orElseThrow(() -> new IllegalStateException("Current user has no tenant assigned."));

        // Extract student IDs and attendance statuses from request parameters
        java.util.Map<Long, AttendanceStatus> statusMap = new java.util.HashMap<>();

        // Form sends data like: status_1=PRESENT, status_2=ABSENT
        for (Map.Entry<String, String> entry : allRequestParams.entrySet()) {
            if (entry.getKey().startsWith("status_")) {
                try {
                    Long studentId = Long.parseLong(entry.getKey().replace("status_", ""));
                    AttendanceStatus status = AttendanceStatus.valueOf(entry.getValue());
                    statusMap.put(studentId, status);
                } catch (Exception ignored) { }
            }
        }

        LocalDate targetDate = (date != null && !date.isEmpty()) ? LocalDate.parse(date) : LocalDate.now();

        try {
            attendanceService.saveBatchAttendance(tenantId, targetDate, statusMap);
            return "redirect:/dashboard/teacher?classId=" + classId + "&date=" + targetDate + "&success=true";
        } catch (Exception e) {
            return "redirect:/dashboard/teacher?classId=" + classId + "&date=" + targetDate + "&error=Failed+to+save+attendance";
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