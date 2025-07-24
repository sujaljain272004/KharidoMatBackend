package com.SpringProject.kharidoMat.controller;

import com.SpringProject.kharidoMat.dto.ChatMessageDTO;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.UserRepository; // Import UserRepository
import com.SpringProject.kharidoMat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UserRepository userRepository; // Use UserRepository to find the user

    /**
     * Endpoint to start a new chat or retrieve an existing one.
     *
     * @param recipientId The ID of the user to chat with.
     * @param authentication The security context to identify the current logged-in user.
     * @return A response entity containing the unique chat room ID.
     */
    @PostMapping("/start/{recipientId}")
    public ResponseEntity<?> startChat(@PathVariable Long recipientId, Authentication authentication) {
        // Get the currently logged-in user's email from the security context
        String senderEmail = authentication.getName();
        
        // Find the sender's User entity from the database
        User sender = userRepository.findByEmail(senderEmail);
        if (sender == null) {
            throw new RuntimeException("Authenticated user not found in database");
        }

        if (sender.getId().equals(recipientId)) {
            return ResponseEntity.badRequest().body("You cannot start a chat with yourself.");
        }

        String chatRoomId = chatService.getOrCreateChatRoom(sender.getId(), recipientId);
        // Return the chat ID in a JSON object for easy parsing on the frontend
        return ResponseEntity.ok(Map.of("chatId", chatRoomId));
    }

    /**
     * Endpoint to get the message history for a specific chat room.
     *
     * @param chatId The ID of the chat room.
     * @return A list of chat messages.
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<List<ChatMessageDTO>> getChatMessages(@PathVariable String chatId) {
        List<ChatMessageDTO> messages = chatService.getChatMessages(chatId)
                .stream()
                .map(ChatMessageDTO::new) // Convert entities to DTOs
                .collect(Collectors.toList());
        return ResponseEntity.ok(messages);
    }
    

}
