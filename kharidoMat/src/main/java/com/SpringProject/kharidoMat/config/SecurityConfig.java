package com.SpringProject.kharidoMat.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.HttpServletResponse;

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
        logger.info("SecurityConfig initialized with JwtAuthFilter");
    }

 // In com.SpringProject.kharidoMat.config.SecurityConfig

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring SecurityFilterChain...");

        http
            .cors(withDefaults())
            .csrf(csrf -> csrf.disable())

            // ✅ DEFINE ONCE: Use stateless sessions for a REST API
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ✅ DEFINE ONCE: Return a 401 error for unauthenticated API requests instead of redirecting
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized")
                )
            )

            // ✅ DEFINE ONCE: All your authorization rules go in this single block
            .authorizeHttpRequests(auth -> {
                auth
                    // --- PUBLIC ENDPOINTS (No login required) ---
                    .requestMatchers(
                        "/api/users/register",
                        "/api/users/complete-registration",
                        "/api/users/login",
                        "/api/users/verify",
                        "/api/users/forgot-password",
                        "/api/users/reset-password",
                        "/api/items/all",
                        "/api/items/search",
                        "/api/items/{id}",
                        "/api/items/category/**",
                        "/api/items/image/{fileName}",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/ws/**",
                        "/oauth2/**",
                        "/login/oauth2/**",
                        "/oauth2/authorization/**"
                    ).permitAll()

                    // --- PROTECTED ENDPOINTS ---
                    .requestMatchers(
                        "/api/dashboard/stats/**",
                        "/api/users/wishlist/**",
                        "/api/users/edit-profile",
                        "/api/items/post",
                        "/api/items/my",
                        "/api/items/upload-image/**",
                        "/api/bookings/**",
                        "/api/chats/**"
                    ).authenticated()

                    // --- CATCH-ALL: Any other request must be authenticated ---
                    .anyRequest().authenticated();
            })
            .oauth2Login(oauth2 -> oauth2
                    .successHandler(oAuth2SuccessHandler)
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

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
        configuration.setAllowedOrigins(List.of("http://localhost:5173","http://127.0.0.1:5173/"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Auth-Token"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}