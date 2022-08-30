package com.stackabuse.kafka_data_pipeline.config;

import com.stackabuse.kafka_data_pipeline.model.Stock;
import com.stackabuse.kafka_data_pipeline.model.StockProfile;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfiguration {

    @Value(value = "${spring.kafka.producer.bootstrap-servers}")
    private String bootstrapServer;

    @Bean
    public ProducerFactory<String, Stock> stockProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, StockProfile> stockProfileProducerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Stock> stockKafkaTemplate() {
        return new KafkaTemplate<>(stockProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, StockProfile> stockProfileKafkaTemplate() {
        return new KafkaTemplate<>(stockProfileProducerFactory());
    }
}