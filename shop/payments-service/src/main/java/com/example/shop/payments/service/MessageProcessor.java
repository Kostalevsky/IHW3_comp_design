package com.example.shop.payments.service;

import com.example.shop.common.events.OrderCreatedEvent;
import com.example.shop.common.events.PaymentResultEvent;
import com.example.shop.payments.entity.InboxMessage;
import com.example.shop.payments.entity.OutboxEvent;
import com.example.shop.payments.repository.InboxRepository;
import com.example.shop.payments.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class MessageProcessor {
    private final InboxRepository inboxRepo;
    private final OutboxRepository outboxRepo;
    private final AccountService accountService;
    private final ObjectMapper mapper;

    public MessageProcessor(InboxRepository inboxRepo, OutboxRepository outboxRepo, AccountService accountService, ObjectMapper mapper, KafkaTemplate<String, String> kafkaTemplate) {
        this.inboxRepo = inboxRepo;
        this.outboxRepo = outboxRepo;
        this.accountService = accountService;
        this.mapper = mapper;
    }

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void processInbox() throws Exception {
        List<InboxMessage> messages = inboxRepo.findByStatus("NEW");
        for (InboxMessage msg : messages) {
            try {
                OrderCreatedEvent event = mapper.readValue(msg.getPayload(), OrderCreatedEvent.class);
                boolean success = accountService.tryDebit(event.getUserId(), event.getAmount());
                PaymentResultEvent result = new PaymentResultEvent(event.getOrderId(), success, success ? null : "Недостаточно средств на счете");
                String payload = mapper.writeValueAsString(result);
                outboxRepo.save(new OutboxEvent(UUID.randomUUID(), payload, "NEW", Instant.now()));
                msg.setStatus("PROCESSED");
                msg.setProcessedAt(Instant.now());
            } catch (Exception e) {
                msg.setStatus("FAILED");
            }
            inboxRepo.save(msg);
        }
    }
}
