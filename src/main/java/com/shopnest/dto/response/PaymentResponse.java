package com.shopnest.dto.response;

import lombok.Data;

@Data
public class PaymentResponse {
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String currency;
    private Double amount;
    private String status;
    private String message;
    private String keyId;
}