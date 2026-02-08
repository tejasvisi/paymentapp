package com.example.paymentapp;



import com.razorpay.Order;
import com.razorpay.Utils;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
@Service
public class RazorpayService {

    @Value("${razorpay.key_id}")
    private String razorpayKeyId;

    @Value("${razorpay.key_secret}")
    private String razorpayKeySecret;
    
    private static final Logger paymentLogger =
            LoggerFactory.getLogger("PAYMENT_LOGGER");
    public Order createOrder(Double amount) throws Exception {
    	try {
    		paymentLogger.info("Payment started");
    		System.out.println("razorpayKeySecret: "+razorpayKeySecret);
    		paymentLogger.info("razorpayKeyId: ", razorpayKeyId);
    		paymentLogger.info("razorpayKeySecret: ", razorpayKeySecret);
        RazorpayClient client = new RazorpayClient(razorpayKeyId, razorpayKeySecret);
        
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", amount * 100); // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "txn_" + System.currentTimeMillis());
        orderRequest.put("payment_capture", true);

        return client.orders.create(orderRequest);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		paymentLogger.error("Error in payment order create", e);
           return null;
    	}
    }
    
    public boolean verifySignature(String orderId, String paymentId, String razorpaySignature) {
        try {
        	paymentLogger.info("verifySignature is called");
        	JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", razorpaySignature);
            boolean verified=Utils.verifyPaymentSignature(options, razorpayKeySecret);
            return verified;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
