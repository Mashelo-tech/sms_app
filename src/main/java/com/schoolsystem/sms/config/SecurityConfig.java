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
    private final TenantContextFilter tenantContextFilter;

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
            .addFilterAfter(tenantContextFilter, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class)
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/login", "/images/**", "/css/**", "/js/**").permitAll()

                // Admin-only: User management
                .requestMatchers("/users/**", "/register-user").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER")

                // Teachers & above: Manage teachers (DOS+ can manage)
                .requestMatchers("/teachers/**", "/register-teacher").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER")

                // Teacher personal day-to-day tasks
                .requestMatchers("/teacher/**").hasAnyRole("TEACHER", "CLASS_TEACHER")

                // Results entry: Teachers and above
                .requestMatchers("/results/**").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER", "TEACHER")

                // Bursar/Fee tracking
                .requestMatchers("/bursar/**").hasAnyRole("HEADTEACHER", "SUPER_DOS", "BURSAR", "ACCOUNTANT", "SECRETARY")

                // Reports: All authenticated users
                .requestMatchers("/reports/**").authenticated()

                // Dashboards: Strict Role-Based Access
                .requestMatchers("/dashboard/teacher").hasAnyRole("TEACHER", "SUPER_DOS", "DOS", "HEADTEACHER")
                .requestMatchers("/dashboard/secretary").hasAnyRole("SECRETARY", "SUPER_DOS", "DOS", "HEADTEACHER")
                .requestMatchers("/").hasAnyRole("SUPER_DOS", "DOS", "HEADTEACHER")

                // Fallback
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
