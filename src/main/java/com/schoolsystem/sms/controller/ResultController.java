package com.schoolsystem.sms.controller;

import com.schoolsystem.sms.dto.MarkEntryBatchDTO;
import com.schoolsystem.sms.dto.MarkEntryDTO;
import com.schoolsystem.sms.model.*;
import com.schoolsystem.sms.repository.*;
import com.schoolsystem.sms.service.ResultService;
import com.schoolsystem.sms.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/results")
@RequiredArgsConstructor
public class ResultController {

    private final ResultService resultService;
    private final UserService userService;
    private final ClassLevelRepository classLevelRepository;
    private final SubjectRepository subjectRepository;
    private final TermRepository termRepository;
    private final StudentRepository studentRepository;
    private final ResultRepository resultRepository;
    private final TeacherAssignmentRepository teacherAssignmentRepository;

    // ─── RESULTS ENTRY PAGE ───────────────────────────────────────────────────
    @GetMapping
    public String showResultsPage(
            @RequestParam(required = false) Long classId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Long termId,
            Model model,
            Authentication authentication) {

        addCommonAttributes(model, authentication);

        // Always load filter options
        model.addAttribute("classLevels", classLevelRepository.findAll());
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("activeTerm", termRepository.findByActiveTrue().orElse(null));

        // Default to active term if none selected
        if (termId == null) {
            termRepository.findByActiveTrue().ifPresent(t -> model.addAttribute("selectedTermId", t.getId()));
        } else {
            model.addAttribute("selectedTermId", termId);
        }
        model.addAttribute("selectedClassId", classId);
        model.addAttribute("selectedSubjectId", subjectId);

        // If all three filters are selected → load student marks grid
        if (classId != null && subjectId != null && termId != null) {
            ClassLevel classLevel = classLevelRepository.findById(classId).orElse(null);
            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            Term term = termRepository.findById(termId).orElse(null);

            if (classLevel != null && subject != null && term != null) {
                model.addAttribute("selectedClass", classLevel);
                model.addAttribute("selectedSubject", subject);
                model.addAttribute("selectedTerm", term);

                List<Student> students = studentRepository.findByCurrentClass(classLevel);
                MarkEntryBatchDTO batch = new MarkEntryBatchDTO();
                batch.setClassLevelId(classId);
                batch.setSubjectId(subjectId);
                batch.setTermId(termId);

                List<MarkEntryDTO> entries = new ArrayList<>();
                for (Student s : students) {
                    // Pre-fill existing marks if any
                    Optional<Result> existing = resultRepository
                            .findByStudentAndTerm(s, term)
                            .stream()
                            .filter(r -> r.getSubject().getId().equals(subjectId))
                            .findFirst();

                    MarkEntryDTO entry = new MarkEntryDTO();
                    entry.setStudentId(s.getId());
                    entry.setStudentName(s.getFullName());
                    entry.setRegistrationNumber(s.getRegistrationNumber());
                    entry.setMarks(existing.map(Result::getMarks).orElse(null));
                    entries.add(entry);
                }
                batch.setEntries(entries);
                model.addAttribute("markBatch", batch);
                model.addAttribute("students", students);

                // Also load existing results for this combo (for status display)
                List<Result> existingResults = new ArrayList<>();
                for (Student s : students) {
                    resultRepository.findByStudentAndTerm(s, term).stream()
                            .filter(r -> r.getSubject().getId().equals(subjectId))
                            .findFirst()
                            .ifPresent(existingResults::add);
                }
                model.addAttribute("existingResults", existingResults);
            }
        }

        return "results";
    }

    // ─── SAVE MARKS (BATCH) ───────────────────────────────────────────────────
    @PostMapping("/save")
    public String saveMarks(
            @ModelAttribute MarkEntryBatchDTO batch,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();
        int saved = 0;
        List<String> errors = new ArrayList<>();

        for (MarkEntryDTO entry : batch.getEntries()) {
            if (entry.getMarks() == null) continue; // skip blanks
            try {
                resultService.saveResult(
                        entry.getStudentId(),
                        batch.getSubjectId(),
                        batch.getTermId(),
                        entry.getMarks(),
                        username
                );
                saved++;
            } catch (Exception e) {
                errors.add("Student ID " + entry.getStudentId() + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Saved " + saved + " records. Errors: " + String.join("; ", errors));
        } else {
            redirectAttributes.addFlashAttribute("successMessage", "✅ " + saved + " marks saved successfully.");
        }

        return "redirect:/results?classId=" + batch.getClassLevelId()
                + "&subjectId=" + batch.getSubjectId()
                + "&termId=" + batch.getTermId();
    }

    // ─── SUBMIT FOR REVIEW ────────────────────────────────────────────────────
    @PostMapping("/submit")
    public String submitResults(
            @RequestParam List<Long> resultIds,
            @RequestParam Long classId,
            @RequestParam Long subjectId,
            @RequestParam Long termId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        resultService.submitResults(resultIds, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage",
                "✅ " + resultIds.size() + " result(s) submitted for review.");

        return "redirect:/results?classId=" + classId + "&subjectId=" + subjectId + "&termId=" + termId;
    }

    // ─── DOS APPROVAL PAGE ────────────────────────────────────────────────────
    @GetMapping("/approve")
    @PreAuthorize("hasAnyRole('DOS','SUPER_DOS','HEADTEACHER')")
    public String showApprovalPage(
            @RequestParam(required = false) Long termId,
            Model model,
            Authentication authentication) {

        addCommonAttributes(model, authentication);
        model.addAttribute("terms", termRepository.findAll());
        model.addAttribute("activeTerm", termRepository.findByActiveTrue().orElse(null));

        if (termId != null) {
            Term term = termRepository.findById(termId).orElse(null);
            if (term != null) {
                model.addAttribute("selectedTerm", term);
                model.addAttribute("selectedTermId", termId);
                List<Result> submitted = resultRepository.findByTermAndStatus(term, ResultStatus.SUBMITTED);
                model.addAttribute("submittedResults", submitted);
            }
        } else {
            termRepository.findByActiveTrue().ifPresent(t -> {
                model.addAttribute("selectedTermId", t.getId());
                model.addAttribute("selectedTerm", t);
                model.addAttribute("submittedResults",
                        resultRepository.findByTermAndStatus(t, ResultStatus.SUBMITTED));
            });
        }

        return "results-approve";
    }

    // ─── APPROVE RESULTS ─────────────────────────────────────────────────────
    @PostMapping("/approve")
    @PreAuthorize("hasAnyRole('DOS','SUPER_DOS','HEADTEACHER')")
    public String approveResults(
            @RequestParam List<Long> resultIds,
            @RequestParam(required = false) Long termId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            resultService.approveResults(resultIds, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "✅ " + resultIds.size() + " result(s) approved.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        String redirect = "/results/approve";
        if (termId != null) redirect += "?termId=" + termId;
        return "redirect:" + redirect;
    }

    // ─── RETURN RESULTS ───────────────────────────────────────────────────────
    @PostMapping("/return")
    @PreAuthorize("hasAnyRole('DOS','SUPER_DOS','HEADTEACHER')")
    public String returnResults(
            @RequestParam List<Long> resultIds,
            @RequestParam(required = false) Long termId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        try {
            resultService.returnResults(resultIds, authentication.getName());
            redirectAttributes.addFlashAttribute("successMessage",
                    "⚠️ " + resultIds.size() + " result(s) returned for correction.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        String redirect = "/results/approve";
        if (termId != null) redirect += "?termId=" + termId;
        return "redirect:" + redirect;
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
