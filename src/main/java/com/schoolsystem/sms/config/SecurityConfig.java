package com.schoolsystem.sms.config;

import com.schoolsystem.sms.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/login", "/images/**", "/css/**", "/js/**").permitAll()

                // Admin-only: User management
                .requestMatchers("/users/**", "/register-user").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER")

                // Teachers & above: Manage teachers (secretary can view, DOS+ can manage)
                .requestMatchers("/teachers/**", "/register-teacher").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER", "SECRETARY")

                // Results entry: Teachers and above
                .requestMatchers("/results/**").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER", "TEACHER", "SECRETARY")

                // Reports: All authenticated users
                .requestMatchers("/reports/**").authenticated()

                // Main dashboard: All authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    // Role-based redirect after login
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    switch (role) {
                        case "ROLE_TEACHER"    -> response.sendRedirect("/dashboard/teacher");
                        case "ROLE_SECRETARY"  -> response.sendRedirect("/dashboard/secretary");
                        case "ROLE_HEADTEACHER"-> response.sendRedirect("/");
                        default                -> response.sendRedirect("/"); // DOS, SUPER_DOS
                    }
                })
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
