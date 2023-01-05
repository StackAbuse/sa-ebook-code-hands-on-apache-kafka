package com.stackabuse.kafka_spring_reactive_sse.config;

import com.stackabuse.kafka_spring_reactive_sse.model.Article;
import org.json.simple.JSONObject;
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
    public ReceiverOptions<String, Article> kafkaReceiverOptions(
            @Value(value = "${spring.kafka.consumer.topic}") String topic,
            KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Article> basicReceiverOptions = ReceiverOptions.create(
                kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Article> reactiveKafkaConsumerTemplate(
            ReceiverOptions<String, Article> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }
}
