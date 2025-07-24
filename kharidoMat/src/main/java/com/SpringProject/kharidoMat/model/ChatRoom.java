package com.SpringProject.kharidoMat.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chat_rooms")
public class ChatRoom {

    @Id
    private String id; // Using a UUID for the chat room ID for uniqueness

    // The user who initiated the chat
    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    // The user who is the recipient of the chat
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    // A chat room has a list of messages.
    // 'mappedBy = "chatRoom"' indicates that the ChatMessage entity owns the relationship.
    // CascadeType.ALL means that if we delete a chat room, all its messages are also deleted.
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    public ChatRoom() {
        // Automatically generate a unique ID when a new chat room is created
        this.id = UUID.randomUUID().toString();
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }
}
