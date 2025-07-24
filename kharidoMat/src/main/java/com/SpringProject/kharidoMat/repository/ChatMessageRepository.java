package com.SpringProject.kharidoMat.repository;

import com.SpringProject.kharidoMat.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    /**
     * Finds all messages belonging to a specific chat room, ordered by their timestamp.
     * This is used to retrieve the chat history for a conversation.
     *
     * @param chatRoomId The ID of the chat room.
     * @return A list of ChatMessage entities, sorted from oldest to newest.
     */
    List<ChatMessage> findByChatRoomIdOrderByTimestampAsc(String chatRoomId);

}