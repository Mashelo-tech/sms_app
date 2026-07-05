package com.schoolsystem.sms.config;

import com.schoolsystem.sms.model.Role;
import com.schoolsystem.sms.model.User;
import com.schoolsystem.sms.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {


    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            logger.info("=====================================================");
            logger.info("CHECKING DATABASE FOR ADMIN USER...");
            
            if (userRepository.findByUsername("admin").isEmpty()) {
                logger.info("Admin not found. Injecting default admin now...");
                
                User admin = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin123")) 
                        .fullName("System Administrator")
                        .email("admin@schoolsystem.com")
                        .role(Role.SUPER_DOS)
                        .enabled(true)
                        .build();

                userRepository.save(admin);
                logger.info("✅ SUCCESS: Default Admin User created (admin / admin123)");
            } else {
                logger.info("✅ SKIPPED: Admin user already exists in the database.");
            }
            logger.info("=====================================================");
        };
    }
}