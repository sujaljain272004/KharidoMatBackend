package com.SpringProject.kharidoMat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        logger.info("SecurityConfig initialized with JwtAuthFilter");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain...");

        http
            .csrf(csrf -> {
                csrf.disable();
                logger.debug("CSRF disabled");
            })
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers(
                        "/api/users/register",
                        "/api/users/complete-registration",
                        "/api/users/login",          
                        "/api/users/verify",
                        "/api/users/forgot-password",
                        "/api/users/reset-password",
                        "/api/items/search", 
                        "/api/items/**", 
                        "/api/items/image/**", 
                        "/api/items/category/**",
                        "/api/users/wishlist/**",
                        "/api/test/**", 
                        "/ws/**",
                        "/api/return/**",
                        "/swagger-ui/**",
                        "/v3/api-docs/**"
                    ).permitAll();
                logger.debug("Public endpoints permitted");

                auth.anyRequest().authenticated();
                logger.debug("Other endpoints require authentication");
            })
            .sessionManagement(session -> {
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                logger.debug("Session management set to STATELESS");
            })
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("BCryptPasswordEncoder bean created");
        return new BCryptPasswordEncoder();
    }
}
