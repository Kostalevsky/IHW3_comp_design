package com.example.shop.common.events;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class OrderCreatedEvent implements Serializable {
    private UUID orderId;
    private UUID userId;
    private BigDecimal amount;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(UUID orderId, UUID userId, BigDecimal amount) {
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderCreatedEvent)) {
            return false;
        }
        OrderCreatedEvent that = (OrderCreatedEvent) o;
        return Objects.equals(orderId, that.orderId) && Objects.equals(userId, that.userId) && Objects.equals(amount, that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, userId, amount);
    }

    @Override
    public String toString() {
        return "OrderCreatedEvent{" + "orderId=" + orderId + ", userId=" + userId + ", amount=" + amount + '}';
    }
}