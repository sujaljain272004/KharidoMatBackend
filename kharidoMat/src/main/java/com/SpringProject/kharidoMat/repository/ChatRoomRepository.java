package com.SpringProject.kharidoMat.repository;

import com.SpringProject.kharidoMat.model.ChatRoom;
import com.SpringProject.kharidoMat.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, String> {

    /**
     * Finds a chat room between two specific users, regardless of who is the sender or recipient.
     * This is crucial for preventing duplicate chat rooms between the same two people.
     *
     * @param user1 The first user in the chat.
     * @param user2 The second user in the chat.
     * @return An Optional containing the ChatRoom if found, otherwise empty.
     */
    // --- THIS METHOD IS NOW CORRECTED ---
    Optional<ChatRoom> findBySenderAndRecipientOrRecipientAndSender(User user1, User user2, User user3, User user4);


    /**
     * +++ THIS IS THE NEW METHOD +++
     * Finds all chat rooms where the given user is either the sender or the recipient.
     * This is used to build the user's "Inbox" or "My Chats" list.
     *
     * @param sender The user entity.
     * @param recipient The same user entity.
     * @return A list of all chat rooms the user is a part of.
     */
    List<ChatRoom> findBySenderOrRecipient(User sender, User recipient);

}
