package com.SpringProject.kharidoMat.config;

import java.security.Principal;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

@Component
public class CustomHandshakeHandler extends DefaultHandshakeHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomHandshakeHandler.class);

    @Override
    protected Principal determineUser(ServerHttpRequest request,
                                      WebSocketHandler wsHandler,
                                      Map<String, Object> attributes) {
        String email = (String) attributes.get("user");
        logger.info("WebSocket handshake initiated for user: {}", email);
        return () -> email;
    }
}
