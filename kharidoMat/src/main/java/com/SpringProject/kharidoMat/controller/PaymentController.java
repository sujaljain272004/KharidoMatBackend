package com.SpringProject.kharidoMat.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.service.RazorpayService;
import com.razorpay.Order;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private RazorpayService razorpayService;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestParam int amount) {
        logger.info("Creating Razorpay order for amount: {}", amount);
        try {
            Order order = razorpayService.createOrder(amount);
            logger.info("Razorpay order created successfully: {}", order.get("id").toString());
            return ResponseEntity.ok(order.toString());
        } catch (Exception e) {
            logger.error("Error creating Razorpay order: {}", e.getMessage());
            return ResponseEntity.status(500).body("Error creating Razorpay order: " + e.getMessage());
        }
    }
}
