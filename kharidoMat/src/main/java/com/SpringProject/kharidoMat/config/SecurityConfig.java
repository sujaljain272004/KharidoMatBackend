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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
        logger.info("SecurityConfig initialized with JwtAuthFilter");
    }

 // In com.SpringProject.kharidoMat.config.SecurityConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain...");

        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeHttpRequests(auth -> {
                // --- PUBLIC ENDPOINTS (No login required) ---
                auth.requestMatchers(
                        // Authentication
                        "/api/users/register",
                        "/api/users/complete-registration",
                        "/api/users/login",
                        "/api/users/verify",
                        "/api/users/forgot-password",
                        "/api/users/reset-password",
                        // Public Item routes
                        "/api/items/all",
                        "/api/items/search",
                        "/api/items/{id}",
                        "/api/items/category/**",
                        "/api/items/image/**",
                        "/api/items/upload-image",
                        "/api/items/image/{fileName}",
                        "/api/users/edit-profile",
                        // Swagger & Docs
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        // WebSocket connection
                        "/ws/**"
                    ).permitAll()

                    // --- PROTECTED ENDPOINTS (Login required) ---
                    .requestMatchers(
                        // User-specific data
                        "/api/users/dashboard", 
                        "/api/users/wishlist/**",
                        "/api/users/edit-profile",
                        // Item management
                        "/api/items/post",
                        "/api/items/my",
                        "/api/items/upload-image/**",
                        // Booking management
                        "/api/bookings/**"
                    ).authenticated()

                    // --- DEFAULT RULE ---
                    // Any other request not listed above must be authenticated.
                    .anyRequest().authenticated();
            });

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.debug("BCryptPasswordEncoder bean created");
        return new BCryptPasswordEncoder();
    }
    
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // IMPORTANT: Change this to your frontend's actual URL
        configuration.setAllowedOrigins(List.of("http://localhost:5173"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Auth-Token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
