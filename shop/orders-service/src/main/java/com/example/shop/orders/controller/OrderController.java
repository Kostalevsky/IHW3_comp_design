package com.example.shop.orders.controller;

import com.example.shop.common.dto.CreateOrderRequest;
import com.example.shop.common.dto.OrderResponse;
import com.example.shop.orders.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Orders", description = "Управление заказами")
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @Operation(summary = "Создать новый заказ")
    @PostMapping
    public ResponseEntity<UUID> create(@RequestBody CreateOrderRequest req) {
        UUID orderId = service.createOrder(req.getUserId(), req.getAmount());
        return ResponseEntity.status(201).body(orderId);
    }

    @Operation(summary = "Получить заказ по ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> get(@PathVariable UUID orderId) {
        OrderResponse resp = service.getOrder(orderId);
        return ResponseEntity.ok(resp);
    }

    @Operation(summary = "Список всех заказов пользователя")
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> list(@PathVariable UUID userId) {
        List<OrderResponse> list = service.listOrders(userId);
        return ResponseEntity.ok(list);
    }
}