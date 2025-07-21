package com.SpringProject.kharidoMat.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.SpringProject.kharidoMat.util.JwtUtil;

import java.io.IOException;
import java.util.Collections;
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Skip filtering for public paths    @Override
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        return path.equals("/api/users/register")
                || path.equals("/api/users/login")
                || path.equals("/api/users/verify")
                || path.equals("/api/users/forgot-password")
                || path.equals("/api/users/reset-password")
                || path.startsWith("/api/items")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/ws")
                || path.startsWith("/api/test")
                || path.startsWith("/api/return");
          
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        logger.debug("JWT Auth Filter triggered for URI: {}", path);

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            logger.debug("Token extracted: {}", token);

            String userEmail = jwtUtil.extractEmail(token);
            logger.debug("Email extracted from token: {}", userEmail);

            if (userEmail != null && jwtUtil.isTokenValid(token)) {
                logger.info("Valid JWT token for user: {}", userEmail);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userEmail, null, Collections.emptyList());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                logger.warn("Invalid or expired JWT token");
            }
        } else {
            logger.warn("Authorization header missing or doesn't start with Bearer");
        }

        filterChain.doFilter(request, response);
    }
}
