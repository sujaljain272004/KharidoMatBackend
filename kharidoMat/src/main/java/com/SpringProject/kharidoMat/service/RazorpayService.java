package com.SpringProject.kharidoMat.service;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;

@Service
public class RazorpayService {

    private RazorpayClient client;

    public RazorpayService() throws Exception {
    	this.client = new RazorpayClient("rzp_test_YNVZR4J3cdxcV5", "43u2cVM31zVRr5ZPFZxSEBwi");
    }

    public Order createOrder(int amountInRupees) throws Exception {
        JSONObject options = new JSONObject();
        options.put("amount", amountInRupees * 100); 
        options.put("currency", "INR");
        options.put("receipt", "txn_" + System.currentTimeMillis());

        return client.orders.create(options);
    }
}
