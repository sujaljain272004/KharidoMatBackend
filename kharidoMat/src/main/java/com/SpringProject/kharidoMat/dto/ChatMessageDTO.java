package com.SpringProject.kharidoMat.dto;

import com.SpringProject.kharidoMat.model.ChatMessage;
import java.time.LocalDateTime;

public class ChatMessageDTO {

    private Long id;
    private Long senderId;
    private String senderName;
    private String chatRoomId;
    private String content;
    private LocalDateTime timestamp;

    // This constructor converts a ChatMessage database entity into a safe DTO
    public ChatMessageDTO(ChatMessage message) {
        this.id = message.getId();
        this.senderId = message.getSender().getId();
        this.senderName = message.getSender().getFullName(); // Assumes your User model has a getFullName() method
        this.chatRoomId = message.getChatRoom().getId();
        this.content = message.getContent();
        this.timestamp = message.getTimestamp();
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getChatRoomId() {
        return chatRoomId;
    }

    public void setChatRoomId(String chatRoomId) {
        this.chatRoomId = chatRoomId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
