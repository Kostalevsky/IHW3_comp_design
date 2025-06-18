package com.example.shop.common.events;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class PaymentResultEvent implements Serializable {
    private UUID orderId;
    private boolean success;
    private String reason;

    public PaymentResultEvent() {}

    public PaymentResultEvent(UUID orderId, boolean success, String reason) {
        this.orderId = orderId;
        this.success = success;
        this.reason = reason;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentResultEvent)) {
            return false;
        }
        PaymentResultEvent that = (PaymentResultEvent) o;
        return success == that.success && Objects.equals(orderId, that.orderId) && Objects.equals(reason, that.reason);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, success, reason);
    }

    @Override
    public String toString() {
        return "PaymentResultEvent{" + "orderId=" + orderId + ", success=" + success + ", reason='" + reason + '\'' + '}';
    }
}