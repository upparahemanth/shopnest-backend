package com.shopnest.service;

import com.shopnest.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiatePayment(Long orderId);
    PaymentResponse verifyPayment(String razorpayOrderId,
            String razorpayPaymentId, String razorpaySignature);
}