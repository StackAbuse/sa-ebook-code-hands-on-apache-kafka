package com.stackabuse.springbootkafka.service;

import com.stackabuse.springbootkafka.model.InventoryItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class ConsumeInventoryService {

    @KafkaListener(id = "${spring.kafka.consumer.group-id}",
            topics = "${spring.kafka.consumer.topic}",
            groupId = "${spring.kafka.consumer.group-id}",
            autoStartup = "${spring.kafka.consumer.auto-start:true}",
            topicPartitions = @TopicPartition(topic = "${spring.kafka.consumer.topic}",
                    partitions = { "0", "1", "3", "6", "8"}))
    @SendTo("${spring.kafka.consumer.forward-to}")
    @Transactional("kafkaTransactionManager")
    public void listenMessage(@Payload InventoryItem item,
                              @Header(name = KafkaHeaders.RECEIVED_MESSAGE_KEY, required = false) Integer key,
                              @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                              @Header(KafkaHeaders.GROUP_ID) String groupId,
                              @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
                              @Header(KafkaHeaders.RECEIVED_TIMESTAMP) long ts) {
        log.info("Received Message {} with key {} in topic {} as part of group {} at partition {} at timestamp {}.",
                item,
                key,
                topic,
                groupId,
                partition,
                ts);
    }

    @KafkaListener(topics = "${spring.kafka.consumer.forward-to}")
    @Transactional("kafkaTransactionManager")
    public void listenForwarderMessage(InventoryItem item) {
        log.info("Received Message in forwarder: {}", item);
    }
}
