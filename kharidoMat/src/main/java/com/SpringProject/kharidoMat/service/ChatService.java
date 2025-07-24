package com.SpringProject.kharidoMat.service;

import com.SpringProject.kharidoMat.model.ChatRoom;
import com.SpringProject.kharidoMat.model.User;
import com.SpringProject.kharidoMat.model.ChatMessage;
import com.SpringProject.kharidoMat.repository.ChatRoomRepository;
import com.SpringProject.kharidoMat.repository.ChatMessageRepository;
import com.SpringProject.kharidoMat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Finds an existing chat room between two users or creates a new one if none exists.
     * This is the main entry point when a user wants to start a chat.
     *
     * @param senderId The ID of the user initiating the chat.
     * @param recipientId The ID of the user receiving the chat request.
     * @return The unique ID of the chat room.
     */
    @Transactional
    public String getOrCreateChatRoom(Long senderId, Long recipientId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Sender not found with ID: " + senderId));
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found with ID: " + recipientId));

        // Check for an existing chat room in both directions
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository
                .findBySenderAndRecipientOrRecipientAndSender(sender, recipient, recipient, sender);

        if (chatRoomOpt.isPresent()) {
            return chatRoomOpt.get().getId(); // Return existing chat room ID
        } else {
            // Create a new chat room if one doesn't exist
            ChatRoom newChatRoom = new ChatRoom();
            newChatRoom.setSender(sender);
            newChatRoom.setRecipient(recipient);
            ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
            return savedChatRoom.getId(); // Return the new chat room ID
        }
    }

    /**
     * Saves a new chat message to the database.
     *
     * @param message The ChatMessage object to be saved.
     * @return The saved ChatMessage entity.
     */
    public ChatMessage saveMessage(ChatMessage message) {
        message.setTimestamp(LocalDateTime.now()); // Set the timestamp before saving
        return chatMessageRepository.save(message);
    }

    /**
     * Retrieves the entire message history for a given chat room.
     *
     * @param chatRoomId The ID of the chat room.
     * @return A list of all messages in that chat room, ordered by time.
     */
    public List<ChatMessage> getChatMessages(String chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByTimestampAsc(chatRoomId);
    }

    /**
     * +++ THIS IS THE NEW METHOD +++
     * Retrieves all chat rooms for a given user.
     *
     * @param userEmail The email of the user.
     * @return A list of all chat rooms the user is a part of.
     */
    public List<ChatRoom> getChatRoomsForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user != null) {
            return chatRoomRepository.findBySenderOrRecipient(user, user);
        }
        return Collections.emptyList(); // Return an empty list if user is not found
    }

}
