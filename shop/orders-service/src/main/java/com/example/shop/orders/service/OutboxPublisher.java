package com.example.shop.orders.service;

import com.example.shop.orders.entity.OutboxEvent;
import com.example.shop.orders.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class OutboxPublisher {
    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);
    private final OutboxRepository outboxRepo;
    private final KafkaTemplate<String,String> kafka;

    public OutboxPublisher(OutboxRepository outboxRepo, KafkaTemplate<String,String> kafka) {
        this.outboxRepo = outboxRepo;
        this.kafka = kafka;
    }

    @Scheduled(fixedDelay = 3000)
    @Transactional
    public void publishOutbox() {
        List<OutboxEvent> events = outboxRepo.findByStatus("NEW");
        for (OutboxEvent e : events) {
            try {
                kafka.send("order-created", e.getPayload()).get();
                e.setStatus("SENT");
                e.setCreatedAt(Instant.now());
                log.info("Публикация события {}", e.getEventId());
            } catch (Exception ex) {
                log.error("Ошибка при публикации события {}", e.getEventId(), ex);
            }
            outboxRepo.save(e);
        }
    }
}