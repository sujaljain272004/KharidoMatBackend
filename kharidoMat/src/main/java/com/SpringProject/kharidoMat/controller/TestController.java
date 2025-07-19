package com.SpringProject.kharidoMat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.service.EmailService;

@RestController
@RequestMapping("/api/test")
public class TestController {

    private static final Logger logger = LoggerFactory.getLogger(TestController.class);

    @Autowired
    private EmailService emailService;

    @GetMapping("/send")
    public String testEmail(@RequestParam String to) {
        logger.info("Sending test email to {}", to);
        emailService.sendEmail(to, "Test Email", "Hello Sujal, This is a test email");
        logger.info("Test email sent successfully to {}", to);
        return "Email sent to " + to;
    }
}
