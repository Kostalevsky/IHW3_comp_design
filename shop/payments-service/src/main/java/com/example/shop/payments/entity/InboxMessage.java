package com.example.shop.payments.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "inbox_messages")
public class InboxMessage {

    @Id
    @Column(name = "message_id", nullable = false)
    private UUID messageId;

    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt;

    @Column(name = "processed_at")
    private Instant processedAt;

    public InboxMessage() {
    }

    public InboxMessage(UUID messageId, String payload, String status, Instant receivedAt) {
        this.messageId = messageId;
        this.payload = payload;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    public UUID getMessageId() {
        return messageId;
    }

    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Instant receivedAt) {
        this.receivedAt = receivedAt;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(Instant processedAt) {
        this.processedAt = processedAt;
    }

    @Override
    public String toString() {
        return "InboxMessage{" + "messageId=" + messageId + ", status='" + status + '\'' + ", receivedAt=" + receivedAt + ", processedAt=" + processedAt + '}';
    }
}