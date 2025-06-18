package com.example.shop.common.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class OrderResponse {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;
    private String status;
    private Instant createdAt;

    public OrderResponse() {}
    public OrderResponse(UUID orderId, UUID userId, BigDecimal amount, String status, Instant createdAt) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}