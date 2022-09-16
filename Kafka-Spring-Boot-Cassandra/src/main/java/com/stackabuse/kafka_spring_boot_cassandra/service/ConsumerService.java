package com.stackabuse.kafka_spring_boot_cassandra.service;

import com.stackabuse.kafka_spring_boot_cassandra.repository.OrderRepository;
import com.stackabuse.kafka_spring_boot_cassandra.schema.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class ConsumerService {

    @Autowired
    OrderRepository orderRepository;

    @KafkaListener(topics = "cassandra-order",
            autoStartup = "true")
    public void consumeOrderMessage(Order order) {
        log.info("Consumed Order message: {}", order);

        com.stackabuse.kafka_spring_boot_cassandra.entity.Order orderEntity =
                com.stackabuse.kafka_spring_boot_cassandra.entity.Order
                .builder()
                .id(UUID.fromString(order.getId().toString()))
                .productName(order.getProductName().toString())
                .productId(order.getProductId().toString())
                .productType(order.getProductType().toString())
                .productCount(order.getProductCount())
                .listingDate(order.getListingDate().toString())
                .customerId(order.getCustomerId().toString())
                .customerEmail(order.getCustomerEmail().toString())
                .customerName(order.getCustomerName().toString())
                .customerMobile(order.getCustomerMobile().toString())
                .shippingAddress(order.getShippingAddress().toString())
                .shippingPincode(order.getShippingPincode().toString())
                .status(order.getStatus().toString())
                .price(order.getPrice())
                .weight(order.getWeight())
                .automatedEmail(order.getAutomatedEmail())
                .build();

        com.stackabuse.kafka_spring_boot_cassandra.entity.Order savedOrder = orderRepository.save(orderEntity);
        log.info("Order saved into Cassandra: {}", savedOrder);

    }
}
