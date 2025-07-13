package com.SpringProject.kharidoMat.model;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Login request containing user's email and password")
public class LoginRequest {

    @Schema(
        description = "Email address registered with the application",
        example = "sujal@college.edu",
        required = true
    )
    private String email;

    @Schema(
        description = "Password associated with the user's account",
        example = "mySecret123",
        required = true
    )
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
