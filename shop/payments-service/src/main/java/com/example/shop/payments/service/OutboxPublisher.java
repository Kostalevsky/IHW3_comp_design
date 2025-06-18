package com.example.shop.payments.service;

import com.example.shop.payments.entity.OutboxEvent;
import com.example.shop.payments.repository.OutboxRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class OutboxPublisher {
    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    public OutboxPublisher(OutboxRepository outboxRepo, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepo = outboxRepo;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishOutbox() {
        List<OutboxEvent> events = outboxRepo.findByStatus(Status.NEW.name());
        for (OutboxEvent evt : events) {
            try {
                kafkaTemplate.send("payment-results", evt.getPayload()).get();
                evt.setStatus(Status.SENT.name());
                evt.setProcessedAt(Instant.now());
            } catch (Exception ex) {
                log.error("Ошибка публикации события ", evt.getEventId(), ex);
            }
            outboxRepo.save(evt);
        }
    }
}