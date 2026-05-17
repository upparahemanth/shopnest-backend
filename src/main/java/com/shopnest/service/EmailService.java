package com.shopnest.service;

import com.shopnest.dto.response.OrderResponse;

public interface EmailService {
    void sendWelcomeEmail(String toEmail, String name);
    void sendOrderConfirmationEmail(String toEmail, String name,
            OrderResponse order);
    void sendOrderStatusUpdateEmail(String toEmail, String name,
            Long orderId, String status);
    void sendPaymentSuccessEmail(String toEmail, String name,
            Long orderId, Double amount, String paymentId);
}