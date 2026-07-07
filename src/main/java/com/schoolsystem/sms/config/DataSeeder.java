package com.schoolsystem.sms.config;

import com.schoolsystem.sms.model.*;
import com.schoolsystem.sms.repository.*;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataSeeder {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner initDatabase(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            ClassLevelRepository classLevelRepository,
            SubjectRepository subjectRepository,
            AcademicYearRepository academicYearRepository,
            TermRepository termRepository,
            GradingScaleRepository gradingScaleRepository
    ) {
        return args -> {
            logger.info("=====================================================");
            logger.info("SEEDING DATABASE...");

            // --- Seed Default Users (one per role) ---
            seedUser(userRepository, passwordEncoder, "admin",       "admin123",     "System Administrator",    "admin@school.com",      Role.SUPER_DOS);
            seedUser(userRepository, passwordEncoder, "dos",         "dos123",       "Director of Studies",     "dos@school.com",        Role.DOS);
            seedUser(userRepository, passwordEncoder, "headteacher", "head123",      "Head Teacher",            "head@school.com",       Role.HEADTEACHER);
            seedUser(userRepository, passwordEncoder, "secretary",   "secretary123", "School Secretary",        "secretary@school.com",  Role.SECRETARY);
            seedUser(userRepository, passwordEncoder, "teacher1",    "teacher123",   "Mr. James Okello",        "jokello@school.com",    Role.TEACHER);

            // --- Seed Class Levels ---
            if (classLevelRepository.count() == 0) {
                classLevelRepository.saveAll(List.of(
                    ClassLevel.builder().name("Primary 1").levelOrder(1).build(),
                    ClassLevel.builder().name("Primary 2").levelOrder(2).build(),
                    ClassLevel.builder().name("Primary 3").levelOrder(3).build(),
                    ClassLevel.builder().name("Primary 4").levelOrder(4).build(),
                    ClassLevel.builder().name("Primary 5").levelOrder(5).build(),
                    ClassLevel.builder().name("Primary 6").levelOrder(6).build(),
                    ClassLevel.builder().name("Primary 7").levelOrder(7).build()
                ));
                logger.info("✅ Seeded Class Levels (P1 - P7)");
            }

            // --- Seed Subjects ---
            if (subjectRepository.count() == 0) {
                subjectRepository.saveAll(List.of(
                    Subject.builder().code("ENG").name("English Language").build(),
                    Subject.builder().code("MTC").name("Mathematics").build(),
                    Subject.builder().code("SCI").name("Science").build(),
                    Subject.builder().code("SST").name("Social Studies").build()
                ));
                logger.info("✅ Seeded Subjects");
            }

            // --- Seed Academic Year & Terms ---
            if (academicYearRepository.count() == 0) {
                AcademicYear year2025 = AcademicYear.builder()
                        .name("2025")
                        .startDate(LocalDate.of(2025, 2, 1))
                        .endDate(LocalDate.of(2025, 11, 30))
                        .active(false)
                        .build();
                year2025 = academicYearRepository.save(year2025);

                AcademicYear year2026 = AcademicYear.builder()
                        .name("2026")
                        .startDate(LocalDate.of(2026, 2, 1))
                        .endDate(LocalDate.of(2026, 11, 30))
                        .active(true)
                        .build();
                year2026 = academicYearRepository.save(year2026);

                termRepository.saveAll(List.of(
                    Term.builder().name("Term 1").startDate(LocalDate.of(2026, 2, 1)).endDate(LocalDate.of(2026, 4, 30)).active(false).academicYear(year2026).build(),
                    Term.builder().name("Term 2").startDate(LocalDate.of(2026, 5, 1)).endDate(LocalDate.of(2026, 8, 31)).active(true).academicYear(year2026).build(),
                    Term.builder().name("Term 3").startDate(LocalDate.of(2026, 9, 1)).endDate(LocalDate.of(2026, 11, 30)).active(false).academicYear(year2026).build()
                ));
                logger.info("✅ Seeded Academic Year 2026 with Terms");
            }

            // --- Seed Grading Scale (Uganda Primary PLE style) ---
            if (gradingScaleRepository.count() == 0) {
                gradingScaleRepository.saveAll(List.of(
                    GradingScale.builder().minMark(90).maxMark(100).grade("D1").points(1).comment("Distinction").build(),
                    GradingScale.builder().minMark(80).maxMark(89).grade("D2").points(2).comment("Distinction").build(),
                    GradingScale.builder().minMark(70).maxMark(79).grade("C3").points(3).comment("Credit").build(),
                    GradingScale.builder().minMark(65).maxMark(69).grade("C4").points(4).comment("Credit").build(),
                    GradingScale.builder().minMark(60).maxMark(64).grade("C5").points(5).comment("Credit").build(),
                    GradingScale.builder().minMark(55).maxMark(59).grade("C6").points(6).comment("Credit").build(),
                    GradingScale.builder().minMark(45).maxMark(54).grade("P7").points(7).comment("Pass").build(),
                    GradingScale.builder().minMark(40).maxMark(44).grade("P8").points(8).comment("Pass").build(),
                    GradingScale.builder().minMark(0).maxMark(39).grade("F9").points(9).comment("Fail").build()
                ));
                logger.info("✅ Seeded Grading Scale (Uganda Primary PLE style D1–F9)");
            }

            logger.info("=====================================================");
        };
    }

    private void seedUser(UserRepository repo, PasswordEncoder encoder,
                          String username, String password, String fullName, String email, Role role) {
        if (repo.findByUsername(username).isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .password(encoder.encode(password))
                    .fullName(fullName)
                    .email(email)
                    .role(role)
                    .enabled(true)
                    .build();
            repo.save(user);
            logger.info("✅ Created user: {} ({})", username, role);
        } else {
            logger.info("⏭  SKIPPED: User '{}' already exists.", username);
        }
    }
}