package com.stackabuse.springcloudkafka.publisher;

import com.github.javafaker.Faker;
import com.stackabuse.springcloudkafka.binder.OrderBinder;
import com.stackabuse.springcloudkafka.model.Order;
import com.stackabuse.springcloudkafka.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OrderPublisher {

    @Autowired
    private OrderBinder orderBinder;

    @Scheduled(initialDelay = 5_000, fixedDelay = 5_000)
    private void publish() {
        Order order = Order.builder()
                .id(UUID.randomUUID().toString())
                .productName(Faker.instance().commerce().productName())
                .productId(Faker.instance().idNumber().ssnValid())
                .productType(Faker.instance().commerce().department())
                .productCount(Faker.instance().random().nextInt(1, 5))
                .listingDate(Instant
                        .ofEpochMilli(Faker
                                .instance()
                                .date()
                                .past(3, TimeUnit.DAYS)
                                .getTime())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime())
                .customerId(Faker.instance().idNumber().invalid())
                .customerName(Faker.instance().artist().name())
                .customerEmail(Faker.instance().internet().emailAddress())
                .customerMobile(Faker.instance().phoneNumber().cellPhone())
                .shippingAddress(Faker.instance().address().fullAddress())
                .shippingPincode(Faker.instance().address().zipCode())
                .status(OrderStatus.PLACED)
                .price(Double.parseDouble(Faker.instance().commerce().price()))
                .build();
        log.info("Order Checked Out from Website/App -> {}", order);
        orderBinder
                .orderCheckout()
                .send(MessageBuilder
                        .withPayload(order)
                        .setHeader(KafkaHeaders.MESSAGE_KEY, order.getId().getBytes())
                .build());
    }
}
