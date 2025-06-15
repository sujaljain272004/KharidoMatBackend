package com.SpringProject.kharidoMat.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.SpringProject.kharidoMat.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
