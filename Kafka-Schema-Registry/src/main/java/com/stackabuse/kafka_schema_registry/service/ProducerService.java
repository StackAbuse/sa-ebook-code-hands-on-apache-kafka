package com.stackabuse.kafka_schema_registry.service;

import com.stackabuse.kafka_schema_registry.schema.Order;
import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ProducerService {

    @Autowired
    private KafkaTemplate<String, Order> kafkaTemplate;

    @PostConstruct
    @Transactional
    public void sendMessage() throws ParseException {
        for (int i = 0; i < 10; i++) {
            Order order = Order.newBuilder()
                    .setId(UUID.randomUUID().toString())
                    .setProductName(Faker.instance().commerce().productName())
                    .setProductId(Faker.instance().idNumber().ssnValid())
                    .setProductType(Faker.instance().commerce().department())
                    .setProductCount(Faker.instance().random().nextInt(1, 5))
                    .setListingDate(Instant
                            .ofEpochMilli(Faker
                                    .instance()
                                    .date()
                                    .past(3, TimeUnit.DAYS)
                                    .getTime())
                            .atZone(ZoneId.systemDefault())
                            .toLocalDateTime()
                            .toString())
                    .setCustomerId(Faker.instance().idNumber().invalid())
                    .setCustomerName(Faker.instance().artist().name())
                    .setCustomerEmail(Faker.instance().internet().emailAddress())
                    .setCustomerMobile(Faker.instance().phoneNumber().cellPhone())
                    .setShippingAddress(Faker.instance().address().fullAddress())
                    .setShippingPincode(Faker.instance().address().zipCode())
                    .setStatus("ORDERED")
                    .setPrice(Float.parseFloat(Faker.instance().commerce().price()))
                    .setWeight(Float.parseFloat(Faker.instance().commerce().price()))
                    .setAutomatedEmail(true)
                    .build();

            ListenableFuture<SendResult<String, Order>> future =
                    kafkaTemplate.send("schema-registry-order", order.getId().toString(), order);
            future.addCallback(new ListenableFutureCallback<SendResult<String, Order>>() {

                @Override
                public void onSuccess(final SendResult<String, Order> message) {
                    log.info("sent message= " + message + " with offset= " + message.getRecordMetadata().offset());
                }

                @Override
                public void onFailure(final Throwable throwable) {
                    log.error("unable to send message= " + order, throwable);
                }
            });
        }
    }
}
