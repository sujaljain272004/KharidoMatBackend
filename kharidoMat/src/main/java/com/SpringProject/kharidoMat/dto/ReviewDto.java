// src/main/java/com/yourproject/dto/ReviewDto.java
package com.SpringProject.kharidoMat.dto;

import java.time.LocalDateTime;

import com.SpringProject.kharidoMat.model.User;

public class ReviewDto {
    private Long id;
    private int rating;
    private String comment;
    private LocalDateTime localDateTime;
    private UserDTO user; // This will hold the user's details

    // Getters and Setters for all fields...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getLocalDateTime() { return localDateTime; }
    public void setLocalDateTime(LocalDateTime localDateTime) { this.localDateTime = localDateTime; }
    public UserDTO getUser() { return user; }
    public void setUser(UserDTO user) { this.user = user; }
}