package com.stackabuse.springbootkafka.controller;

import com.stackabuse.springbootkafka.model.InventoryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping(value = "/inventory")
public class PublishInventoryController {

    @Value(value = "${spring.kafka.producer.topic}")
    private String topic;

    @Autowired
    private KafkaTemplate<String, InventoryItem> kafkaTemplate;

    @PostMapping(value = "/publish")
    @Transactional("kafkaTransactionManager")
    public void sendMessage(@RequestParam("name") String name, @RequestParam("count") int count) {
        InventoryItem item = InventoryItem.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .count(count)
                .listingDate(new Date())
                .build();
        log.info(String.format("Message sent to inventory -> %s", item));
        kafkaTemplate.executeInTransaction(t -> t.send(topic, item));
//        kafkaTemplate.send(topic, item);
    }

    @PostMapping(value = "/publish-with-callback")
    public void sendMessageWithCallback(@RequestParam("name") String name, @RequestParam("count") int count) {
        InventoryItem item = InventoryItem.builder()
                .id(UUID.randomUUID().toString())
                .name(name)
                .count(count)
                .listingDate(new Date())
                .build();
        log.info(String.format("Message with callback sent to inventory -> %s", item));
        ListenableFuture<SendResult<String, InventoryItem>> future = kafkaTemplate.send(topic, item);
        future.addCallback(new ListenableFutureCallback<SendResult<String, InventoryItem>>() {

            @Override
            public void onSuccess(SendResult<String, InventoryItem> result) {
                log.info("Sent message=[" + item +
                        "] with offset=[" + result.getRecordMetadata().offset() + "]");
            }

            @Override
            public void onFailure(Throwable ex) {
                log.error("Unable to send message=[" + item + "] due to error : " + ex.getMessage());
            }
        });
    }
}
