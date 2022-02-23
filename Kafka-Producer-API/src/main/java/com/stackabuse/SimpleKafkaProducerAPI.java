package com.stackabuse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stackabuse.constants.ApplicationConstants;
import com.stackabuse.model.Notification;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Properties;

public class SimpleKafkaProducerAPI {

    public static void main(String[] args) throws JsonProcessingException {
        final Logger logger = LoggerFactory.getLogger(SimpleKafkaProducerAPI.class);
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        final Producer<String, String> producer = new KafkaProducer<>(props);
        logger.info("========================================================================================");
        logger.info("Starting Kafka Producer Process");
        producer.send(
                new ProducerRecord<>(
                    ApplicationConstants.TOPIC,
                    "111-11111-1111",
                    new Notification(
                        new Date().toString(),
                        "111-11111-1111",
                        "Your order has been processed and shipped!!",
                        "222-2222-2222",
                        "John Doe",
                        "john.doe@example.com"
                    ).toJson()
                )
        );
        logger.info("Flushing and closing producer");
        logger.info("========================================================================================");
        producer.flush();
        producer.close();
    }
}
