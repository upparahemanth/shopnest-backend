package com.shopnest.serviceImpl;

import com.shopnest.dto.response.OrderResponse;
import com.shopnest.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    // ✅ Send email asynchronously so it doesn't slow down API
    @Async
    private void sendEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to);
        } catch (Exception e) {
            System.err.println("❌ Email sending failed: " + e.getMessage());
        }
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "🎉 Welcome to ShopNest!";
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px;
                        margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #1976d2, #42a5f5);
                            padding: 30px; border-radius: 12px; text-align: center;">
                    <h1 style="color: white; margin: 0;">🛍️ ShopNest</h1>
                    <p style="color: #e3f2fd; margin: 8px 0 0 0;">
                        Your one-stop shopping destination
                    </p>
                </div>

                <div style="padding: 30px 0;">
                    <h2 style="color: #333;">Welcome, %s! 🎉</h2>
                    <p style="color: #555; line-height: 1.6;">
                        Thank you for joining ShopNest! We're excited to have
                        you as part of our community.
                    </p>
                    <p style="color: #555; line-height: 1.6;">
                        Start exploring our amazing collection of products
                        at unbeatable prices!
                    </p>

                    <div style="text-align: center; margin: 30px 0;">
                        <a href="http://localhost:5173"
                           style="background-color: #1976d2; color: white;
                                  padding: 14px 32px; border-radius: 8px;
                                  text-decoration: none; font-weight: bold;
                                  font-size: 16px;">
                            🛍️ Start Shopping
                        </a>
                    </div>
                </div>

                <div style="border-top: 1px solid #eee; padding-top: 20px;
                            text-align: center; color: #aaa; font-size: 13px;">
                    <p>© 2026 ShopNest. All rights reserved.</p>
                    <p>This email was sent to %s</p>
                </div>
            </div>
            """.formatted(name, toEmail);

        sendEmail(toEmail, subject, html);
    }

    @Override
    public void sendOrderConfirmationEmail(String toEmail, String name,
            OrderResponse order) {
        String subject = "✅ Order Confirmed — #" + order.getId();

        // Build order items HTML
        StringBuilder itemsHtml = new StringBuilder();
        order.getOrderItems().forEach(item -> {
            itemsHtml.append("""
                <tr>
                    <td style="padding: 12px; border-bottom: 1px solid #f0f0f0;">
                        %s
                    </td>
                    <td style="padding: 12px; border-bottom: 1px solid #f0f0f0;
                               text-align: center;">
                        %d
                    </td>
                    <td style="padding: 12px; border-bottom: 1px solid #f0f0f0;
                               text-align: right; font-weight: bold; color: #1976d2;">
                        ₹%s
                    </td>
                </tr>
                """.formatted(
                    item.getProductName(),
                    item.getQuantity(),
                    String.format("%,.0f", item.getPrice() * item.getQuantity())
            ));
        });

        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px;
                        margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #1976d2, #42a5f5);
                            padding: 30px; border-radius: 12px; text-align: center;">
                    <h1 style="color: white; margin: 0;">🛍️ ShopNest</h1>
                    <p style="color: #e3f2fd;">Order Confirmation</p>
                </div>

                <div style="padding: 30px 0;">
                    <h2 style="color: #2e7d32;">✅ Order Confirmed!</h2>
                    <p style="color: #555;">Hi <strong>%s</strong>,</p>
                    <p style="color: #555; line-height: 1.6;">
                        Your order has been placed successfully!
                        Here are your order details:
                    </p>

                    <div style="background: #f8f9fa; border-radius: 8px;
                                padding: 16px; margin: 20px 0;">
                        <p style="margin: 4px 0; color: #555;">
                            <strong>Order ID:</strong> #%d
                        </p>
                        <p style="margin: 4px 0; color: #555;">
                            <strong>Status:</strong>
                            <span style="color: #e65100;">⏳ PENDING</span>
                        </p>
                        <p style="margin: 4px 0; color: #555;">
                            <strong>Date:</strong> %s
                        </p>
                    </div>

                    <table style="width: 100%%; border-collapse: collapse;">
                        <thead>
                            <tr style="background-color: #1976d2; color: white;">
                                <th style="padding: 12px; text-align: left;">
                                    Product
                                </th>
                                <th style="padding: 12px; text-align: center;">
                                    Qty
                                </th>
                                <th style="padding: 12px; text-align: right;">
                                    Price
                                </th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                        <tfoot>
                            <tr>
                                <td colspan="2"
                                    style="padding: 16px; font-weight: bold;
                                           font-size: 16px;">
                                    Total
                                </td>
                                <td style="padding: 16px; font-weight: bold;
                                           font-size: 18px; color: #1976d2;
                                           text-align: right;">
                                    ₹%s
                                </td>
                            </tr>
                        </tfoot>
                    </table>

                    <div style="text-align: center; margin: 30px 0;">
                        <a href="http://localhost:5173/orders"
                           style="background-color: #1976d2; color: white;
                                  padding: 14px 32px; border-radius: 8px;
                                  text-decoration: none; font-weight: bold;">
                            📦 View My Orders
                        </a>
                    </div>
                </div>

                <div style="border-top: 1px solid #eee; padding-top: 20px;
                            text-align: center; color: #aaa; font-size: 13px;">
                    <p>© 2026 ShopNest. All rights reserved.</p>
                </div>
            </div>
            """.formatted(
                name,
                order.getId(),
                order.getOrderDate().toString().replace("T", " ").substring(0, 16),
                itemsHtml.toString(),
                String.format("%,.0f", order.getTotalAmount())
        );

        sendEmail(toEmail, subject, html);
    }

    @Override
    public void sendOrderStatusUpdateEmail(String toEmail, String name,
            Long orderId, String status) {

        String emoji = switch (status) {
            case "CONFIRMED" -> "✅";
            case "SHIPPED" -> "🚚";
            case "DELIVERED" -> "🎉";
            case "CANCELLED" -> "❌";
            default -> "⏳";
        };

        String subject = emoji + " Order #" + orderId + " — " + status;
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px;
                        margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #1976d2, #42a5f5);
                            padding: 30px; border-radius: 12px; text-align: center;">
                    <h1 style="color: white; margin: 0;">🛍️ ShopNest</h1>
                    <p style="color: #e3f2fd;">Order Status Update</p>
                </div>

                <div style="padding: 30px 0; text-align: center;">
                    <div style="font-size: 64px; margin-bottom: 16px;">%s</div>
                    <h2 style="color: #333;">Order Status Updated!</h2>
                    <p style="color: #555;">Hi <strong>%s</strong>,</p>
                    <p style="color: #555; line-height: 1.6;">
                        Your order <strong>#%d</strong> status has been
                        updated to:
                    </p>

                    <div style="background: #e8f5e9; border-radius: 8px;
                                padding: 16px; margin: 20px 0; display: inline-block;">
                        <span style="font-size: 20px; font-weight: bold;
                                     color: #2e7d32;">
                            %s %s
                        </span>
                    </div>

                    <div style="margin: 30px 0;">
                        <a href="http://localhost:5173/orders"
                           style="background-color: #1976d2; color: white;
                                  padding: 14px 32px; border-radius: 8px;
                                  text-decoration: none; font-weight: bold;">
                            📦 View My Orders
                        </a>
                    </div>
                </div>

                <div style="border-top: 1px solid #eee; padding-top: 20px;
                            text-align: center; color: #aaa; font-size: 13px;">
                    <p>© 2026 ShopNest. All rights reserved.</p>
                </div>
            </div>
            """.formatted(emoji, name, orderId, emoji, status);

        sendEmail(toEmail, subject, html);
    }

    @Override
    public void sendPaymentSuccessEmail(String toEmail, String name,
            Long orderId, Double amount, String paymentId) {
        String subject = "💳 Payment Successful — Order #" + orderId;
        String html = """
            <div style="font-family: Arial, sans-serif; max-width: 600px;
                        margin: 0 auto; padding: 20px;">
                <div style="background: linear-gradient(135deg, #1976d2, #42a5f5);
                            padding: 30px; border-radius: 12px; text-align: center;">
                    <h1 style="color: white; margin: 0;">🛍️ ShopNest</h1>
                    <p style="color: #e3f2fd;">Payment Confirmation</p>
                </div>

                <div style="padding: 30px 0; text-align: center;">
                    <div style="font-size: 64px; margin-bottom: 16px;">🎉</div>
                    <h2 style="color: #2e7d32;">Payment Successful!</h2>
                    <p style="color: #555;">Hi <strong>%s</strong>,</p>
                    <p style="color: #555; line-height: 1.6;">
                        Your payment for order <strong>#%d</strong>
                        has been received successfully!
                    </p>

                    <div style="background: #f8f9fa; border-radius: 8px;
                                padding: 20px; margin: 20px 0; text-align: left;">
                        <p style="margin: 6px 0; color: #555;">
                            <strong>Order ID:</strong> #%d
                        </p>
                        <p style="margin: 6px 0; color: #555;">
                            <strong>Amount Paid:</strong>
                            <span style="color: #1976d2; font-weight: bold;">
                                ₹%s
                            </span>
                        </p>
                        <p style="margin: 6px 0; color: #555;">
                            <strong>Payment ID:</strong> %s
                        </p>
                        <p style="margin: 6px 0; color: #555;">
                            <strong>Status:</strong>
                            <span style="color: #2e7d32; font-weight: bold;">
                                ✅ PAID
                            </span>
                        </p>
                    </div>

                    <div style="margin: 30px 0;">
                        <a href="http://localhost:5173/orders"
                           style="background-color: #1976d2; color: white;
                                  padding: 14px 32px; border-radius: 8px;
                                  text-decoration: none; font-weight: bold;">
                            📦 View My Orders
                        </a>
                    </div>
                </div>

                <div style="border-top: 1px solid #eee; padding-top: 20px;
                            text-align: center; color: #aaa; font-size: 13px;">
                    <p>© 2026 ShopNest. All rights reserved.</p>
                    <p>Thank you for shopping with ShopNest! 🛍️</p>
                </div>
            </div>
            """.formatted(
                name, orderId, orderId,
                String.format("%,.0f", amount),
                paymentId
        );

        sendEmail(toEmail, subject, html);
    }
}