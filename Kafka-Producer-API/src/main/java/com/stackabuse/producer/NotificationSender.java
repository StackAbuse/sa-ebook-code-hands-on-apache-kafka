package com.stackabuse.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import com.stackabuse.model.Notification;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class NotificationSender implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(NotificationSender.class);
    private final Producer<String, Notification> producer;
    private final int delayMinMs;
    private final int delayMaxMs;
    private final String topic;

    public NotificationSender(final String topic,
                       final Producer<String, Notification> producer,
                       final int delayMinMs,
                       final int delayMaxMs) {
        this.producer = producer;
        this.delayMinMs = delayMinMs;
        this.delayMaxMs = delayMaxMs;
        this.topic = topic;
    }


    public void run() {
        final Random random = new Random(System.currentTimeMillis());
        int sentCount = 0;

        while (true) {
            sentCount++;

            try {
                final ProducerRecord<String, Notification> record = createRandomRecord(random);
                final int delay = randomIntBetween(random, delayMaxMs, delayMinMs);
                logger.info("Sending record: {}", record);
                final Future<RecordMetadata> future = producer.send(record);
                if (sentCount % 100 == 0) {
                    displayRecordMetaData(record, future);
                }
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                if (Thread.interrupted()) {
                    break;
                }
            } catch (ExecutionException e) {
                logger.error("problem sending record to producer", e);
            } catch (ParseException | JsonProcessingException e) {
                logger.error("problem while parsing the record", e);
            }
        }
    }

    private void displayRecordMetaData(final ProducerRecord<String, Notification> record,
                                       final Future<RecordMetadata> future)
            throws InterruptedException, ExecutionException {
        final RecordMetadata recordMetadata = future.get();
        logger.info(String.format("\n\t\t\tkey=%s, value=%s " +
                        "\n\t\t\tsent to topic=%s part=%d off=%d at time=%s",
                record.key(),
                record.value(),
                recordMetadata.topic(),
                recordMetadata.partition(),
                recordMetadata.offset(),
                new Date(recordMetadata.timestamp())
        ));
    }

    private final int randomIntBetween(final Random random,
                                       final int max,
                                       final int min) {
        return random.nextInt(max - min + 1) + min;
    }

    private ProducerRecord<String, Notification> createRandomRecord(final Random random)
            throws ParseException, JsonProcessingException {
        Faker faker = new Faker();
        String orderId = faker.idNumber().valid();
        final Notification notification = new Notification(
                faker.date().past(5, TimeUnit.DAYS).toString(),
                orderId,
                "Your order has been processed and shipped!!",
                faker.idNumber().valid(),
                faker.gameOfThrones().character(),
                faker.internet().safeEmailAddress()
        );
        return new ProducerRecord<>(topic, orderId, notification);
    }
}