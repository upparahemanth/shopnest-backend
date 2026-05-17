package com.shopnest.serviceImpl;

import com.shopnest.dto.request.OrderRequest;
import com.shopnest.dto.response.OrderItemResponse;
import com.shopnest.dto.response.OrderResponse;
import com.shopnest.entity.*;
import com.shopnest.enums.OrderStatus;
import com.shopnest.exception.ApiException;
import com.shopnest.exception.ResourceNotFoundException;
import com.shopnest.repository.*;
import com.shopnest.service.EmailService;
import com.shopnest.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final AddressRepository addressRepository;
    private final ProductRepository productRepository;
    private final EmailService emailService; // ✅ Added

    @Override
    public OrderResponse placeOrder(String email, OrderRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));

        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found"));

        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new ApiException(
                    "Cart is empty. Please add items before placing an order.");
        }

        // ✅ Check stock availability
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            if (product.getStock() < cartItem.getQuantity()) {
                throw new ApiException("Insufficient stock for product: "
                        + product.getName()
                        + ". Available stock: " + product.getStock());
            }
        }

        // Calculate total
        double total = cart.getCartItems().stream()
                .mapToDouble(item ->
                        item.getProduct().getPrice() * item.getQuantity())
                .sum();

        // Build order items
        List<OrderItem> orderItems = cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .product(cartItem.getProduct())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getProduct().getPrice())
                        .build())
                .collect(Collectors.toList());

        // Create order
        Order order = Order.builder()
                .user(user)
                .orderItems(orderItems)
                .totalAmount(total)
                .orderDate(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        orderItems.forEach(item -> item.setOrder(order));
        Order savedOrder = orderRepository.save(order);

        // ✅ Deduct stock
        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepository.save(product);
        }

        // Clear cart
        cart.getCartItems().clear();
        cartRepository.save(cart);

        OrderResponse response = mapToResponse(savedOrder);

        // ✅ Send order confirmation email
        try {
            emailService.sendOrderConfirmationEmail(
                    user.getEmail(), user.getName(), response);
        } catch (Exception e) {
            System.err.println("Order email failed: " + e.getMessage());
        }

        return response;
    }

    @Override
    public List<OrderResponse> getMyOrders(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found"));
        return orderRepository.findByUserId(user.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Order not found with id: " + orderId));
        order.setStatus(OrderStatus.valueOf(status.toUpperCase()));
        Order savedOrder = orderRepository.save(order);

        // ✅ Send status update email
        try {
            emailService.sendOrderStatusUpdateEmail(
                    savedOrder.getUser().getEmail(),
                    savedOrder.getUser().getName(),
                    savedOrder.getId(),
                    status.toUpperCase()
            );
        } catch (Exception e) {
            System.err.println("Status email failed: " + e.getMessage());
        }

        return mapToResponse(savedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setStatus(order.getStatus());

        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setProductId(item.getProduct().getId());
                    itemResponse.setProductName(item.getProduct().getName());
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPrice(item.getPrice());
                    return itemResponse;
                }).collect(Collectors.toList());

        response.setOrderItems(items);
        return response;
    }
}