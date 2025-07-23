package com.SpringProject.kharidoMat.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Hex;

@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String keyId;

    @Value("${razorpay.key_secret}")
    private String keySecret;

    public Order createOrder(double amount) throws Exception {
        RazorpayClient client = new RazorpayClient(keyId, keySecret);

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int)(amount * 100)); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", true);

        return client.orders.create(orderRequest);
    }

    public boolean verifySignature(String orderId, String paymentId, String signature) throws Exception {
        String data = orderId + "|" + paymentId;
        String generatedSignature = hmacSHA256(data, keySecret);
        return generatedSignature.equals(signature);
    }

    private String hmacSHA256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        mac.init(secretKey);
        byte[] hashBytes = mac.doFinal(data.getBytes());
        return Hex.encodeHexString(hashBytes);
    }
}
