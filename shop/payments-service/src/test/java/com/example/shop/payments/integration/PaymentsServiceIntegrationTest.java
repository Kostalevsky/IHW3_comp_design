// package com.example.shop.payments.integration;

// import com.example.shop.common.events.OrderCreatedEvent;
// import com.example.shop.common.events.PaymentResultEvent;
// import com.example.shop.payments.repository.OutboxRepository;
// import com.fasterxml.jackson.databind.ObjectMapper;
// import org.apache.kafka.clients.consumer.ConsumerRecord;
// import org.junit.jupiter.api.AfterAll;
// import org.junit.jupiter.api.BeforeAll;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.util.TestPropertyValues;
// import org.springframework.context.ApplicationContextInitializer;
// import org.springframework.context.ConfigurableApplicationContext;
// import org.springframework.kafka.annotation.EnableKafka;
// import org.springframework.kafka.core.*;
// import org.springframework.kafka.listener.ContainerProperties;
// import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
// import org.apache.kafka.common.serialization.StringDeserializer;
// import org.springframework.kafka.test.EmbeddedKafkaBroker;
// import org.springframework.kafka.test.context.EmbeddedKafka;
// import org.springframework.kafka.test.utils.ContainerTestUtils;
// import org.springframework.test.context.ContextConfiguration;
// import org.springframework.test.annotation.DirtiesContext;

// import java.math.BigDecimal;
// import java.util.Collections;
// import java.util.UUID;
// import java.util.concurrent.BlockingQueue;
// import java.util.concurrent.LinkedBlockingQueue;

// import static org.assertj.core.api.Assertions.assertThat;

// @SpringBootTest
// @DirtiesContext
// @EmbeddedKafka(partitions = 1, topics = { "order-created", "payment-results" }, brokerProperties = {"offsets.topic.replication.factor=1", "transaction.state.log.replication.factor=1", "transaction.state.log.min.isr=1"})
// @ContextConfiguration(initializers = PaymentsServiceIntegrationTest.KafkaInitializer.class)
// @EnableKafka
// class PaymentsServiceIntegrationTest {

//     @Autowired
//     private EmbeddedKafkaBroker embeddedKafka;

//     @Autowired
//     private KafkaTemplate<String,String> kafkaTemplate;

//     @Autowired
//     private OutboxRepository outboxRepo;

//     @Autowired
//     private ObjectMapper mapper;

//     private static BlockingQueue<ConsumerRecord<String,String>> records;

//     @BeforeAll
//     static void setupAll() {
//         records = new LinkedBlockingQueue<>();
//     }

//     @AfterAll
//     static void tearDownAll() {
//         records.clear();
//     }

//     static class KafkaInitializer
//         implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//       @Override
//       public void initialize(ConfigurableApplicationContext ctx) {
//         TestPropertyValues.of(
//             "spring.kafka.bootstrap-servers=" +
//               System.getProperty(EmbeddedKafkaBroker.SPRING_EMBEDDED_KAFKA_BROKERS)
//         ).applyTo(ctx.getEnvironment());
//       }
//     }

//     @Test
//     void fullFlow_orderCreatedEvent_isProcessed_andPaymentResultPublished() throws Exception {
//         ContainerProperties containerProps = new ContainerProperties("payment-results");
//         DefaultKafkaConsumerFactory<String,String> cf = new DefaultKafkaConsumerFactory<>(Collections.singletonMap("bootstrap.servers", embeddedKafka.getBrokersAsString()), new StringDeserializer(), new StringDeserializer());
//         ConcurrentMessageListenerContainer<String,String> container = new ConcurrentMessageListenerContainer<>(cf, containerProps);

//         container.setupMessageListener((org.springframework.kafka.listener.MessageListener<String,String>) record -> {
//             records.add(record);
//         });
//         container.start();
//         ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());

//         UUID orderId = UUID.randomUUID();
//         UUID userId  = UUID.randomUUID();
//         OrderCreatedEvent orderEvent = new OrderCreatedEvent(orderId, userId, BigDecimal.valueOf(42));
//         String payload = mapper.writeValueAsString(orderEvent);
//         kafkaTemplate.send("order-created", payload);

//         boolean outboxReady = false;
//         long start = System.currentTimeMillis();
//         while (System.currentTimeMillis() - start < 10_000) {
//             if (!outboxRepo.findByStatus("NEW").isEmpty()) {
//                 outboxReady = true;
//                 break;
//             }
//             Thread.sleep(500);
//         }
//         assertThat(outboxReady).withFailMessage("OutboxEvent не был создан вовремя").isTrue();

//         ConsumerRecord<String,String> rec = records.poll(10, java.util.concurrent.TimeUnit.SECONDS);
//         assertThat(rec).isNotNull();
//         PaymentResultEvent result = mapper.readValue(rec.value(), PaymentResultEvent.class);
//         assertThat(result.getOrderId()).isEqualTo(orderId);
//         assertThat(result.isSuccess()).isIn(true, false);
//         container.stop();
//     }
// }