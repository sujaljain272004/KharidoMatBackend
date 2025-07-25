// =============== 2. UserRepository.java (Correct) ===============
// This file is correct and includes the necessary findByWishlist_Id method.

package com.SpringProject.kharidoMat.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.SpringProject.kharidoMat.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.wishlist WHERE u.id = :userId")
    User findByIdWithWishlist(@Param("userId") Long userId);

    List<User> findByWishlist_Id(Long itemId);
}

