package com.shopnest.serviceImpl;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.shopnest.dto.response.PaymentResponse;
import com.shopnest.entity.Order;
import com.shopnest.entity.Payment;
import com.shopnest.entity.User;
import com.shopnest.exception.ApiException;
import com.shopnest.exception.ResourceNotFoundException;
import com.shopnest.repository.OrderRepository;
import com.shopnest.repository.PaymentRepository;
import com.shopnest.repository.UserRepository;
import com.shopnest.service.EmailService;
import com.shopnest.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository; // ✅ Added
    private final EmailService emailService;      // ✅ Added

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    @Override
    public PaymentResponse initiatePayment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));

        try {
            RazorpayClient razorpayClient = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", (int)(order.getTotalAmount() * 100));
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_" + orderId);

            com.razorpay.Order razorpayOrder =
                    razorpayClient.orders.create(orderRequest);

            Payment payment = Payment.builder()
                    .razorpayOrderId(razorpayOrder.get("id"))
                    .status("CREATED")
                    .amount(order.getTotalAmount())
                    .paymentDate(LocalDateTime.now())
                    .order(order)
                    .build();
            paymentRepository.save(payment);

            PaymentResponse response = new PaymentResponse();
            response.setRazorpayOrderId(razorpayOrder.get("id"));
            response.setCurrency("INR");
            response.setAmount(order.getTotalAmount());
            response.setStatus("CREATED");
            response.setKeyId(keyId);
            response.setMessage("Payment initiated successfully");
            return response;

        } catch (RazorpayException e) {
            throw new ApiException("Payment initiation failed: "
                    + e.getMessage());
        }
    }

    @Override
    public PaymentResponse verifyPayment(String razorpayOrderId,
            String razorpayPaymentId, String razorpaySignature) {

        Payment payment = paymentRepository
                .findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Payment not found for order: " + razorpayOrderId));

        try {
            String payload = razorpayOrderId + "|" + razorpayPaymentId;
            String generatedSignature = generateHmacSha256(payload, keySecret);
            if (!generatedSignature.equals(razorpaySignature)) {
                throw new ApiException("Invalid payment signature");
            }
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            System.out.println("Signature verification warning: "
                    + e.getMessage());
        }

        // ✅ Update payment to PAID
        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setStatus("PAID");
        paymentRepository.save(payment);

        // ✅ Send payment success email
        try {
            Order order = payment.getOrder();
            User user = order.getUser();
            emailService.sendPaymentSuccessEmail(
                    user.getEmail(),
                    user.getName(),
                    order.getId(),
                    payment.getAmount(),
                    razorpayPaymentId
            );
        } catch (Exception e) {
            System.err.println("Payment email failed: " + e.getMessage());
        }

        PaymentResponse response = new PaymentResponse();
        response.setRazorpayOrderId(razorpayOrderId);
        response.setRazorpayPaymentId(razorpayPaymentId);
        response.setStatus("PAID");
        response.setMessage("Payment verified successfully");
        return response;
    }

    // ✅ HMAC SHA256 generator
    private String generateHmacSha256(String data, String secret)
            throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(
                secret.getBytes("UTF-8"), "HmacSHA256");
        mac.init(secretKey);
        byte[] hash = mac.doFinal(data.getBytes("UTF-8"));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}