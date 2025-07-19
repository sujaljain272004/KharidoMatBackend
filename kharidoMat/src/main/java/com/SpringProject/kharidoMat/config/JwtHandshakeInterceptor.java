package com.SpringProject.kharidoMat.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.SpringProject.kharidoMat.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(JwtHandshakeInterceptor.class);

    private final JwtUtil jwtUtil;

    public JwtHandshakeInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {

        if (request instanceof ServletServerHttpRequest serverRequest) {
            HttpServletRequest servletRequest = serverRequest.getServletRequest();
            String token = servletRequest.getParameter("token"); 

            logger.info("WebSocket handshake attempt with token: {}", token);

            if (token != null && jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);
                attributes.put("user", email);

                logger.info("Token is valid. Email '{}' added to session attributes.", email);
                return true;
            } else {
                logger.warn("Invalid or missing token during WebSocket handshake.");
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        logger.info("WebSocket afterHandshake called.");
    }
}
