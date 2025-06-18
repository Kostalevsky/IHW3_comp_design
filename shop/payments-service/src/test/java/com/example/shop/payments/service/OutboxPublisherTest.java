package com.example.shop.payments.service;
import com.example.shop.payments.entity.OutboxEvent;
import com.example.shop.payments.repository.OutboxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OutboxPublisherTest {
    private OutboxRepository outboxRepo;
    private KafkaTemplate<String, String> kafkaTemplate;
    private OutboxPublisher publisher;

    @BeforeEach
    void setUp() {
        outboxRepo = mock(OutboxRepository.class);
        kafkaTemplate = mock(KafkaTemplate.class);
        publisher = new OutboxPublisher(outboxRepo, kafkaTemplate);
    }

    @Test
    void publishOutbox_sendsAndMarksSent() throws Exception {
        OutboxEvent evt = new OutboxEvent(UUID.randomUUID(), "{\"foo\":\"bar\"}", "NEW", Instant.now());
        when(outboxRepo.findByStatus("NEW")).thenReturn(List.of(evt));

        SettableListenableFuture<SendResult<String, String>> future = new SettableListenableFuture<>();
        future.set(null);

        when(kafkaTemplate.send("payment-results", evt.getPayload())).thenReturn(future);

        publisher.publishOutbox();

        verify(kafkaTemplate).send("payment-results", evt.getPayload());
        assertThat(evt.getStatus()).isEqualTo("SENT");
        verify(outboxRepo).save(evt);
    }
}