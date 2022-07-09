package com.stackabuse.springcloudkafkastreams.publisher;

import com.github.javafaker.Faker;
import com.stackabuse.springcloudkafkastreams.model.Order;
import com.stackabuse.springcloudkafkastreams.model.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class OrderPublisher {

    @Autowired
    private StreamBridge streamBridge;

    @Scheduled(cron = "*/20 * * * * *")
    public void sendOrder(){
        streamBridge.send("producer-out-0", MessageBuilder
                .withPayload(Order.builder()
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
                    .build())
                .setHeader(KafkaHeaders.MESSAGE_KEY, UUID.randomUUID().toString().getBytes())
                .build());
    }
}
