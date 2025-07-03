package com.SpringProject.kharidoMat.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.SpringProject.kharidoMat.service.RazorpayService;
import com.razorpay.Order;


@RestController
@RequestMapping("/api/payment")
public class PaymentController {

	
	  @Autowired
	    private RazorpayService razorpayService;

	    @PostMapping("/create-order")
	    public ResponseEntity<?> createOrder(@RequestParam int amount) {
	        try {
	            Order order = razorpayService.createOrder(amount);
	            return ResponseEntity.ok(order.toString());
	        } catch (Exception e) {
	            return ResponseEntity.status(500).body("Error creating Razorpay order: " + e.getMessage());
	        }
	    }
}



