package com.example.shop.orders.service;

import com.example.shop.common.dto.OrderResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface OrderService {
    UUID createOrder(UUID userId, BigDecimal amount);
    OrderResponse getOrder(UUID orderId);
    List<OrderResponse> listOrders(UUID userId);
    void handlePaymentResult(UUID orderId, boolean success, String reason);
}