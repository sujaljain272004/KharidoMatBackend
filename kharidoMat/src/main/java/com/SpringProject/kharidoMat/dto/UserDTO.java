package com.SpringProject.kharidoMat.dto;

import com.SpringProject.kharidoMat.model.User;

// This DTO represents the safe, public information about a user.
public class UserDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String prn;
    private String academicYear;

    // This constructor converts a User database entity into a safe DTO
    public UserDTO(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName(); 
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.prn = user.getPrn();
        this.academicYear = user.getAcademicYear();
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPrn() {
        return prn;
    }

    public void setPrn(String prn) {
        this.prn = prn;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }
}
