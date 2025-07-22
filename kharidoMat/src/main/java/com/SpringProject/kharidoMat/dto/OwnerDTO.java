package com.SpringProject.kharidoMat.dto; // A new package for DTOs is good practice

import com.SpringProject.kharidoMat.model.User;

public class OwnerDTO {
    private Long id;
    private String name;
    private String email;

    // Constructor to convert a User entity to a safe DTO
    public OwnerDTO(User user) {
        this.id = user.getId();
        this.name = user.getFullName(); // Or whatever the name field is in your User model
        this.email = user.getEmail();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}