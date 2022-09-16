package com.stackabuse.kafka_spring_cloud_reactive_cassandra.service;

import com.github.javafaker.Faker;
import com.stackabuse.kafka_spring_cloud_reactive_cassandra.schema.Customer;
import com.stackabuse.kafka_spring_cloud_reactive_cassandra.schema.CustomerKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ProducerService {

    @Autowired
    private StreamBridge streamBridge;

    @Scheduled(cron = "*/10 * * * * *")
    public void producer() {

        for (int i = 0; i < 10; i++) {
            String department = Faker.instance().commerce().department();
            Customer customer = Customer.newBuilder()
                    .setId(i)
                    .setFirstName(Faker.instance().friends().character())
                    .setLastName(Faker.instance().gameOfThrones().character())
                    .setDepartment(department)
                    .setDesignation(Faker.instance().company().profession())
                    .build();

            CustomerKey customerKey = CustomerKey.newBuilder()
                    .setId(i)
                    .setDepartmentName(department)
                    .build();
            log.info("Producing message: {}", customer);
            streamBridge.send("producer-out-0", MessageBuilder.withPayload(customer)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, customerKey)
                    .build());
        }
    }
}
