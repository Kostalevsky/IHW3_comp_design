package com.example.shop.orders.service;

import com.example.shop.common.events.PaymentResultEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentResultListener {
    private static final Logger log = LoggerFactory.getLogger(PaymentResultListener.class);
    private final OrderService orderService;
    private final ObjectMapper mapper;

    public PaymentResultListener(OrderService orderService, ObjectMapper mapper) {
        this.orderService = orderService;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "payment-results", groupId = "orders-inbox-group")
    public void onPaymentResult(String payload) {
        try {
            PaymentResultEvent evt = mapper.readValue(payload, PaymentResultEvent.class);
            orderService.handlePaymentResult(evt.getOrderId(), evt.isSuccess(), evt.getReason());
            log.info("Производится событие для заказа {}", evt.getOrderId());
        } catch (Exception e) {
            log.error("Ошибка события для заказа: {}", payload, e);
        }
    }
}