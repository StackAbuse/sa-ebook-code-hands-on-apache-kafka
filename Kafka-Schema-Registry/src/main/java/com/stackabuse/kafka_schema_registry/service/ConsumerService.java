package com.stackabuse.kafka_schema_registry.service;

import com.stackabuse.kafka_schema_registry.schema.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumerService {

    @KafkaListener(topics = "schema-registry-order",
            autoStartup = "true")
    public void consumeOrderMessage(Order order) {
        log.info("Consumed Order message: {}", order);
    }
}
