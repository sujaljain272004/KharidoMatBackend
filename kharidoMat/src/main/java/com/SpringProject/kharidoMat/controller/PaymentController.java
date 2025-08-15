package com.SpringProject.kharidoMat.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.dto.PaymentOrderRequestDTO;
import com.SpringProject.kharidoMat.service.BookingService;
import com.SpringProject.kharidoMat.service.RazorpayService;
import com.razorpay.Order;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayService razorpayService;
    
    private final BookingService bookingService;

    public PaymentController(RazorpayService razorpayService,BookingService bookingService) {
        this.razorpayService = razorpayService;
        this.bookingService=bookingService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody PaymentOrderRequestDTO requestDTO) {
        try {
            // The frontend no longer sends the amount. We call the service to calculate it.
            // This reuses the logic we created in the previous step.
            Map<String, Object> orderDetails = bookingService.createPaymentOrderForBooking(requestDTO);

            // The map returned by the service already contains orderId, amount, etc.
            return ResponseEntity.ok(orderDetails);
            
        } catch (Exception e) {
            // Log the full exception for debugging
            // logger.error("Order creation failed", e); 
            return ResponseEntity.status(500).body("Order creation failed: " + e.getMessage());
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> data) {
        try {
            String razorpayPaymentId = data.get("razorpay_payment_id");
            String razorpayOrderId = data.get("razorpay_order_id");
            String razorpaySignature = data.get("razorpay_signature");

            boolean isValid = razorpayService.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);

            if (isValid) {
                return ResponseEntity.ok("✅ Payment verified and booking confirmed.");
            } else {
                return ResponseEntity.status(400).body("❌ Invalid payment signature");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Verification failed: " + e.getMessage());
        }
    }
}
