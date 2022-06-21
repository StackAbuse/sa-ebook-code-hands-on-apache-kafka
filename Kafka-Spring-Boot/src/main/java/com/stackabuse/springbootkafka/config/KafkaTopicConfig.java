package com.stackabuse.springbootkafka.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServers;

    @Value(value = "${spring.kafka.producer.topic}")
    private String topic;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topic)
                .partitions(10)
                .replicas(3)
                .compact()
                .build();
    }

    @Bean
    public KafkaAdmin.NewTopics multipleTopics() {
        return new KafkaAdmin.NewTopics(
               TopicBuilder.name("stackabuse-product-inventory")
                       .build(),
               TopicBuilder.name("product-inventory-bytes")
                       .replicas(1)
                       .build(),
               TopicBuilder.name("fulfillment-station")
                       .partitions(5)
                       .build()
        );
    }
}
