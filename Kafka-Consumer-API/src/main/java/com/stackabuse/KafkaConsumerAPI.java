package com.stackabuse;

import com.stackabuse.constants.ApplicationConstants;
import com.stackabuse.consumer.NotificationDeserializer;
import com.stackabuse.model.Notification;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class KafkaConsumerAPI {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerAPI.class);

    private static Consumer<String, Notification> createConsumer() {
        final Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "KafkaNotificationConsumer");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        //Custom Deserializer
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, NotificationDeserializer.class.getName());
        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100);

        // Create the consumer using props.
        final Consumer<String, Notification> consumer = new KafkaConsumer<>(props);

        // Subscribe to the topic.
        consumer.subscribe(Collections.singletonList(ApplicationConstants.TOPIC));
        return consumer;
    }


    static void runConsumer() {
        try (Consumer<String, Notification> consumer = createConsumer()) {
            final Map<String, Notification> map = new HashMap<>();
            final int giveUp = 1000;
            int noRecordsCount = 0;
            int readCount = 0;
            while (true) {
                final ConsumerRecords<String, Notification> consumerRecords = consumer.poll(1000);
                if (consumerRecords.count() == 0) {
                    noRecordsCount++;
                    if (noRecordsCount > giveUp) break;
                    else continue;
                }
                readCount++;
                consumerRecords.forEach(record -> map.put(record.key(), record.value()));
                if (readCount % 100 == 0) {
                    displayRecordsStatsAndNotification(map, consumerRecords);
                }
                consumer.commitAsync();
            }
        }
        logger.info("Done Consumer Processing");
        logger.info("========================================================================================");
    }

    private static void displayRecordsStatsAndNotification(final Map<String, Notification> notificationMap,
            final ConsumerRecords<String, Notification> consumerRecords) {
        logger.info("New ConsumerRecords par count {} count {} %n",
                consumerRecords.partitions().size(),
                consumerRecords.count());
        notificationMap.forEach((s, notification) ->
                logger.info(String.format("Notification content: %s %n", notification)));
    }


    public static void main(String... args) {
        logger.info("========================================================================================");
        logger.info("Starting Kafka Consumer Process");
        runConsumer();
    }
}
