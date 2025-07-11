package com.SpringProject.kharidoMat.config;

import java.util.Map;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.SpringProject.kharidoMat.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;

/* WebSocket doesn't send HTTP headers like normal APIs,
    so we pass the token via query param and manually 
    validate it during the handshake.
*/
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

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
            if (token != null && jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);
                attributes.put("user", email);
                return true;
            }
        }
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        //nothing
    }
}
