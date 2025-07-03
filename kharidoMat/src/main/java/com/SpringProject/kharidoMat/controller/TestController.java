package com.SpringProject.kharidoMat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SpringProject.kharidoMat.service.EmailService;

@RestController
@RequestMapping("/api/test")
public class TestController {
	
	@Autowired
	private EmailService emailService;
	
	@GetMapping("/send")
	public String testEmail(@RequestParam String to) {
		emailService.sendEmail(to, "Test Email", "Hello Sujal, This a test email");
		return "Email sent to " + to;
	}

}
