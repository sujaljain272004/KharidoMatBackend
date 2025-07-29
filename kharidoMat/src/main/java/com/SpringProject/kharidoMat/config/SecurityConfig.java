// =============== 1. SecurityConfig.java (Final Corrected Version) ===============
// This file contains the fix for the routing conflict.

package com.SpringProject.kharidoMat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Import this
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.util.Arrays;
import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final JwtAuthFilter jwtAuthFilter;
    @Autowired
    private CustomOAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain...");

        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                )
            )
            .authorizeHttpRequests(auth -> {
                auth
                    // --- PUBLIC ENDPOINTS ---
                    .requestMatchers(
                        "/api/users/register",
                        "/api/users/login",
                        "/api/items/all",
                        "/api/items/search",
                        "/api/items/category/**",
                        "/api/users/verify",
                        "/api/users/complete-registration",
                        "/api/items/image/{fileName}",
                        "/swagger-ui/**", "/v3/api-docs/**",
                        "/api/bookings/return/verify-otp/**",
                        "/api/users/forgot-password"
                        // Add other non-protected endpoints here
                    ).permitAll()
                    
                    // Protected endpoints
                    .requestMatchers(
                        "/api/dashboard/stats/**",
                        "/api/users/wishlist/**",
                        "/api/users/edit-profile",
                        "/api/items/post",
                        "/api/items/my",
                        "/api/items/upload-image/**",
                        "/api/bookings/**",
                        "/api/chats/**",
                        "/api/reviews/can-review/{itemId}",
                        "/api/reviews/{itemId}",
                        "/api/bookings/{bookingId}/create-extension-order/**",
                        "/api/bookings/{bookingId}/verify-and-extend/**",
                        "/{itemId}/bookings"
                    ).authenticated()

                    // --- FIX: Be specific that only GET requests for an item ID are public ---
                    .requestMatchers(HttpMethod.GET, "/api/items/{id}").permitAll()
                    
                    // --- FIX: Allow all CORS preflight OPTIONS requests ---
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                    // --- PROTECTED ENDPOINTS ---
                    // Any other request will require authentication.
                    .anyRequest().authenticated();
            })
            .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2SuccessHandler))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        logger.info("SecurityFilterChain configured successfully");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://127.0.0.1:5173", "https://kharidomat-frontend.onrender.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}