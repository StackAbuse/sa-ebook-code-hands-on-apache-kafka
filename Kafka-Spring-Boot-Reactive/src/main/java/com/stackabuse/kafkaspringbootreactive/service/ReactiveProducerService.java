package com.stackabuse.kafkaspringbootreactive.service;

import com.stackabuse.kafkaspringbootreactive.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ReactiveProducerService {

    @Autowired
    private ReactiveKafkaProducerTemplate<String, Order> reactiveKafkaProducerTemplate;

    @Value(value = "${spring.kafka.producer.topic}")
    private String topic;

    public void send(Order order) {
        log.info("Record sent to topic={}, {}={},", topic, Order.class.getSimpleName(), order);
        reactiveKafkaProducerTemplate.send(topic, order)
                .doOnSuccess(senderResult -> log.info("Sent Order: {} at offset : {}", order, senderResult.recordMetadata().offset()))
                .doOnError(throwable -> log.error("Some error occurred while consuming an order due to: {}", throwable.getMessage()))
                .subscribe();
    }
}
