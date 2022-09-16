package com.stackabuse.kafka_spring_cloud_stream_schema_registry.service;

import com.stackabuse.kafka_spring_cloud_stream_schema_registry.schema.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Slf4j
@Service
public class ConsumerService {

    @Bean
    public Consumer<Customer> consumer() {
        return c -> log.info("Consumed customer details: {}", c);
    }
}
