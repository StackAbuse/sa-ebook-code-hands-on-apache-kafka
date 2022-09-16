package com.stackabuse.kafka_spring_cloud_reactive_cassandra.service;

import com.stackabuse.kafka_spring_cloud_reactive_cassandra.repository.CustomerRepository;
import com.stackabuse.kafka_spring_cloud_reactive_cassandra.schema.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.function.Consumer;

@Slf4j
@Service
public class ConsumerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Bean
    public Consumer<Customer> consumer() {
        return customer -> {
            log.info("Consumed customer details: {}", customer);
            com.stackabuse.kafka_spring_cloud_reactive_cassandra.entity.Customer customerEntity =
                    com.stackabuse.kafka_spring_cloud_reactive_cassandra.entity.Customer
                            .builder()
                            .id(customer.getId())
                            .firstName(customer.getFirstName().toString())
                            .lastName(customer.getLastName().toString())
                            .department(customer.getDepartment().toString())
                            .designation(customer.getDesignation().toString())
                            .build();
            Mono<com.stackabuse.kafka_spring_cloud_reactive_cassandra.entity.Customer> savedCustomer =
                    customerRepository.save(customerEntity);
            savedCustomer
                    .log()
                    .subscribe();
        };
    }
}
