package com.stackabuse.kafkaspringbootreactive.config;

import com.stackabuse.kafkaspringbootreactive.model.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class ReactiveKafkaConsumerConfig {
    @Bean
    public ReceiverOptions<String, Order> kafkaReceiverOptions(
            @Value(value = "${spring.kafka.consumer.topic}") String topic,
            KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Order> basicReceiverOptions = ReceiverOptions.create(
                kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Order> reactiveKafkaConsumerTemplate(
            ReceiverOptions<String, Order> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }
}
