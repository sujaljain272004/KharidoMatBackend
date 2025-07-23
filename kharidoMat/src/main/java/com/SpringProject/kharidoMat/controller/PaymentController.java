package com.SpringProject.kharidoMat.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.SpringProject.kharidoMat.service.RazorpayService;
import com.razorpay.Order;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final RazorpayService razorpayService;

    public PaymentController(RazorpayService razorpayService) {
        this.razorpayService = razorpayService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> data) {
        try {
            double amount = Double.parseDouble(data.get("amount").toString());
            Order order = razorpayService.createOrder(amount);

            return ResponseEntity.ok(Map.of(
                "orderId", order.get("id"),
                "amount", order.get("amount"),
                "currency", order.get("currency")
            ));
        } catch (Exception e) {
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
