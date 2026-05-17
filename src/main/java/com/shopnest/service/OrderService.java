package com.shopnest.service;

import com.shopnest.dto.request.OrderRequest;
import com.shopnest.dto.response.OrderResponse;
import java.util.List;

public interface OrderService {
    OrderResponse placeOrder(String email, OrderRequest request);
    List<OrderResponse> getMyOrders(String email);
    List<OrderResponse> getAllOrders();
    OrderResponse updateOrderStatus(Long orderId, String status);
}