package com.SpringProject.kharidoMat.controller;

import java.security.Principal;
import java.time.LocalDateTime;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.SpringProject.kharidoMat.model.ChatMessage;

@Controller
public class ChatMessageController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatMessageController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage, Principal principal) {
        chatMessage.setTime(LocalDateTime.now());
        chatMessage.setSenderEmail(principal.getName());
        messagingTemplate.convertAndSendToUser(chatMessage.getRecieverEmail(), "/queue/messages", chatMessage);
    }
}
