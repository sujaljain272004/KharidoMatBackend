package com.SpringProject.kharidoMat.model;

import java.time.LocalDateTime;

public class ChatMessage {

    private String senderEmail;
    private String recieverEmail;
    private String content;
    private LocalDateTime time;

    public ChatMessage() {
    }

    public ChatMessage(String senderEmail, String recieverEmail, String content, LocalDateTime time) {
        this.senderEmail = senderEmail;
        this.recieverEmail = recieverEmail;
        this.content = content;
        this.time = time;
    }

    public String getSenderEmail() {
        return senderEmail;
    }
    public void setSenderEmail(String senderEmail) {
        this.senderEmail = senderEmail;
    }

    public String getRecieverEmail() {
        return recieverEmail;
    }

    public void setRecieverEmail(String recieverEmail) {
        this.recieverEmail = recieverEmail;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}

