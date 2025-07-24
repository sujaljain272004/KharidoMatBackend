package com.SpringProject.kharidoMat.controller;

import com.SpringProject.kharidoMat.dto.ChatMessageDTO;
import com.SpringProject.kharidoMat.model.ChatMessage;
import com.SpringProject.kharidoMat.model.ChatRoom;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.repository.ChatRoomRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import com.SpringProject.kharidoMat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatMessageController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatService chatService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    /**
     * This method is called when a client sends a message to the "/app/chat.private" destination.
     * It saves the message to the database and then sends it to the correct recipient and sender.
     *
     * @param messagePayload The incoming message data from the client.
     * @param principal The authenticated user sending the message.
     */
    @MessageMapping("/chat.private")
    public void processPrivateMessage(@Payload ChatMessagePayload messagePayload, Principal principal) {
        // 1. Find the sender from the authenticated principal
    	User sender = userRepository.findByEmail(principal.getName());
    	if (sender == null) {
    	    throw new RuntimeException("Sender not found with email: " + principal.getName());
    	}

        // 2. Find the chat room
        ChatRoom chatRoom = chatRoomRepository.findById(messagePayload.getChatId())
                .orElseThrow(() -> new RuntimeException("Chat room not found with ID: " + messagePayload.getChatId()));

        // 3. Create the ChatMessage entity to save to the database
        ChatMessage messageToSave = new ChatMessage();
        messageToSave.setSender(sender);
        messageToSave.setChatRoom(chatRoom);
        messageToSave.setContent(messagePayload.getContent());
        
        // 4. Save the message using the service, which also sets the timestamp
        ChatMessage savedMessage = chatService.saveMessage(messageToSave);

        // 5. Convert the saved message to a DTO for sending over WebSocket
        ChatMessageDTO messageDTO = new ChatMessageDTO(savedMessage);

        // 6. Determine the recipient's email by finding the user in the chat room who is NOT the sender
        String recipientEmail = chatRoom.getRecipient().getId().equals(sender.getId())
                ? chatRoom.getSender().getEmail()
                : chatRoom.getRecipient().getEmail();

        // 7. Send the message to both the recipient's and the sender's private queues
        messagingTemplate.convertAndSendToUser(recipientEmail, "/queue/messages", messageDTO);
        messagingTemplate.convertAndSendToUser(sender.getEmail(), "/queue/messages", messageDTO);
    }
}

/**
 * A simple class to represent the JSON payload sent from the frontend.
 * This can be a nested class or a separate file.
 */
class ChatMessagePayload {
    private String chatId;
    private String content;

    // Getters and Setters
    public String getChatId() { return chatId; }
    public void setChatId(String chatId) { this.chatId = chatId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
