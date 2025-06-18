package com.example.shop.orders.service;

import com.example.shop.common.dto.OrderResponse;
import com.example.shop.common.events.OrderCreatedEvent;
import com.example.shop.orders.entity.Order;
import com.example.shop.orders.entity.OutboxEvent;
import com.example.shop.orders.repository.OrderRepository;
import com.example.shop.orders.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.math.BigDecimal;

@Service
public class OrderServiceImp implements OrderService {
    private final OrderRepository orderRepo;
    private final OutboxRepository outboxRepo;
    private final ObjectMapper mapper;

    public OrderServiceImp(OrderRepository orderRepo, OutboxRepository outboxRepo, ObjectMapper mapper) {
        this.orderRepo = orderRepo;
        this.outboxRepo = outboxRepo;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public UUID createOrder(UUID userId, BigDecimal amount) {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(orderId, userId, amount, "CREATED", Instant.now());
        orderRepo.save(order);
        OrderCreatedEvent evt = new OrderCreatedEvent(orderId, userId, amount);

        String payload;
        try {
            payload = mapper.writeValueAsString(evt);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка сериализации события", e);
        }

        OutboxEvent out = new OutboxEvent(UUID.randomUUID(), payload, "NEW", Instant.now());
        outboxRepo.save(out);
        outboxRepo.save(out);

        return orderId;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(UUID orderId) {
        Order o = orderRepo.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Заказ не найден с id: " + orderId));
        return new OrderResponse(o.getOrderId(), o.getUserId(), o.getAmount(),o.getStatus(), o.getCreatedAt());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(UUID userId) {
        return orderRepo.findAllByUserId(userId).stream().map(o -> new OrderResponse(o.getOrderId(), o.getUserId(), o.getAmount(),o.getStatus(), o.getCreatedAt())).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handlePaymentResult(UUID orderId, boolean success, String reason) {
        Order order = orderRepo.findById(orderId).orElseThrow(() -> new IllegalArgumentException("Заказ не найден: " + orderId));
        order.setStatus(success ? "PAID" : "FAILED");
        orderRepo.save(order);
    }
}