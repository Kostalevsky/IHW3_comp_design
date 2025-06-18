package com.example.shop.payments.service;

import com.example.shop.common.events.OrderCreatedEvent;
import com.example.shop.common.events.PaymentResultEvent;
import com.example.shop.payments.entity.InboxMessage;
import com.example.shop.payments.entity.OutboxEvent;
import com.example.shop.payments.repository.InboxRepository;
import com.example.shop.payments.repository.OutboxRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

class MessageProcessorTest {
    private InboxRepository inboxRepo;
    private OutboxRepository outboxRepo;
    private AccountService accountService;
    private ObjectMapper mapper;
    private KafkaTemplate<String, String> kafkaTemplate;
    private MessageProcessor processor;

    @BeforeEach
    void init() {
        inboxRepo = mock(InboxRepository.class);
        outboxRepo = mock(OutboxRepository.class);
        accountService = mock(AccountService.class);
        mapper = new ObjectMapper();
        kafkaTemplate = mock(KafkaTemplate.class);
        processor = new MessageProcessor(inboxRepo, outboxRepo, accountService, mapper, kafkaTemplate);
    }

    @Test
    void processInbox_failedPayment_createsOutboxWithFailureAndMarksProcessed() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID userId  = UUID.randomUUID();
        BigDecimal amount = BigDecimal.TEN;
        OrderCreatedEvent event = new OrderCreatedEvent(orderId, userId, amount);
        String payload = mapper.writeValueAsString(event);
        InboxMessage msg = new InboxMessage(UUID.randomUUID(), payload, "NEW", Instant.now());
        when(inboxRepo.findByStatus("NEW")).thenReturn(List.of(msg));

        when(accountService.tryDebit(eq(userId), eq(amount))).thenReturn(false);

        processor.processInbox();

        verify(outboxRepo).save(argThat((OutboxEvent out) -> {
            try {
                PaymentResultEvent res = mapper.readValue(out.getPayload(), PaymentResultEvent.class);
                return res.getOrderId().equals(orderId) && !res.isSuccess() && res.getReason() != null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));

        verify(inboxRepo).save(argThat(m -> "PROCESSED".equals(m.getStatus())));
    }
}