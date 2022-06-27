package com.stackabuse.springbootkafka.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.PartitionOffset;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@KafkaListener(id = "multi-"+"${spring.kafka.consumer.group-id}",
        topics = "${spring.kafka.consumer.topic}",
        groupId = "multi-"+"${spring.kafka.consumer.group-id}",
        autoStartup = "${spring.kafka.consumer.@auto-start:true}",
        topicPartitions = @TopicPartition(topic = "${spring.kafka.consumer.topic}", partitionOffsets = {
                @PartitionOffset(partition = "0", initialOffset = "0"),
                @PartitionOffset(partition = "3", initialOffset = "0")}))
public class ConsumeInventoryMultiListenerService {

    @KafkaHandler
    @SendTo("${spring.kafka.consumer.forward-to}")
    void listen(String message) {
        log.info("KafkaHandler-String: {}", message);
    }

    @KafkaHandler(isDefault = true)
    @SendTo("${spring.kafka.consumer.forward-to}")
    @Transactional("kafkaTransactionManager")
    Object listenDefault(Object object) {
        log.info("KafkaHandler-Default: {}", object);
        return object;
    }
}