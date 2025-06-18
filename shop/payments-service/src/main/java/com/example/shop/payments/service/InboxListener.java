package com.example.shop.payments.service;

import com.example.shop.common.events.OrderCreatedEvent;
import com.example.shop.payments.entity.InboxMessage;
import com.example.shop.payments.repository.InboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Component
public class InboxListener {
    private final InboxRepository inboxRepo;
    private final ObjectMapper mapper;
    private static final Logger log = LoggerFactory.getLogger(InboxListener.class);

    public InboxListener(InboxRepository inboxRepo, ObjectMapper mapper) {
        this.inboxRepo = inboxRepo;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "order-created", containerFactory = "kafkaListenerContainerFactory")
    public void onOrderCreated(String payload) {
        try {
            OrderCreatedEvent event = mapper.readValue(payload, OrderCreatedEvent.class);
            InboxMessage msg = new InboxMessage(
                UUID.randomUUID(),
                payload,
                "NEW",
                Instant.now()
            );
            inboxRepo.save(msg);
        } catch (Exception e) {
            log.error("Неправильное событие создания заказа: {}", payload, e);
        }
    }
}