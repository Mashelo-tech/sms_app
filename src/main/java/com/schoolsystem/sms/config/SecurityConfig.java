package com.schoolsystem.sms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    //Enforcing strong BCrypt hashing
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    //Bouncer;Who gets in and where they go
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // TEMPORARILY disable to test if the loop stops
                .authorizeHttpRequests((auth) -> auth
                // Remove "/" from permitAll()
                .requestMatchers( "/login","/images/**","/css/**", "/js/**", "/h2-console/**").permitAll()//let anyone see these pages
                .anyRequest().authenticated() // lock down anything else
            )
            .formLogin((form) -> form
                .loginPage("/login")
                .loginProcessingUrl("/login") 
                .defaultSuccessUrl("/", true) // Redirect to dashboard after login
                .permitAll()
            )
            .logout(logout -> logout 
                .logoutSuccessUrl("/login?logout") // where to go after logout
                .permitAll()
            );
        
        // Allow H2 Console to render frames
        // http.csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"));
        // http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()));
      
        return http.build();
    }
}
