package com.stackabuse;

import com.stackabuse.constants.ApplicationConstants;
import com.stackabuse.model.Notification;
import com.stackabuse.producer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class KafkaProducerAPI {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerAPI.class);

    private static Producer<String, Notification> createProducer() {
        final Properties props = new Properties();
        setupBootstrapAndSerializers(props);
        setupBatchingAndCompression(props);
        setupRetriesInFlightTimeout(props);

        //Set number of acknowledgments - acks - default is all
        props.put(ProducerConfig.ACKS_CONFIG, "all");

        //Install interceptor list - config "interceptor.classes"
        props.put(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG, NotificationProducerInterceptor.class.getName());

        return new KafkaProducer<>(props);
    }

    private static void setupRetriesInFlightTimeout(Properties props) {
        //Only two in-flight messages per Kafka broker connection
        // - max.in.flight.requests.per.connection (default 5)
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 1);

        //Set the number of retries - retries
        props.put(ProducerConfig.RETRIES_CONFIG, 3);

        //Request timeout - request.timeout.ms
        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, 15_000);

        //Only retry after one second.
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, 1_000);
    }

    private static void setupBootstrapAndSerializers(Properties props) {
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "NotificationKafkaProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Custom Serializer - config "value.serializer"
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, NotificationSerializer.class.getName());
    }

    private static void setupBatchingAndCompression(final Properties props) {
        //Linger up to 100 ms before sending batch if size not met
        props.put(ProducerConfig.LINGER_MS_CONFIG, 100);

        //Batch up to 64K buffer sizes.
        props.put(ProducerConfig.BATCH_SIZE_CONFIG,  16_384 * 4);

        //Use Snappy compression for batch compression.
        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
    }

    public static void main(String[] args) throws Exception {
        logger.info("========================================================================================");
        logger.info("Starting Kafka Producer Process");
        //Create Kafka Producer
        final Producer<String, Notification> producer = createProducer();
        //Create NotificationSender list
        final List<NotificationSender> notificationSenders = getNotificationSenderList(producer);

        //Create a thread pool so every notification sender gets it own.
        // Increase by 1 to fit metrics.
        final ExecutorService executorService =
                Executors.newFixedThreadPool(notificationSenders.size() + 1);

        //Run Metrics Generator which is runnable passing to the producer.
        executorService.submit(new MetricsGenerator(producer));

        //Run each notification sender in its own thread.
        notificationSenders.forEach(executorService::submit);


        //Register nice shutdown of thread pool, then flush and close producer.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(200, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                logger.warn("shutting down", e);
            } finally {
                logger.info("Flushing and closing producer");
                logger.info("========================================================================================");
                producer.flush();
                producer.close();
            }
        }));
    }

    private static List<NotificationSender> getNotificationSenderList(
            final Producer<String, Notification> producer) {
        return Collections.singletonList(new NotificationSender(
                ApplicationConstants.TOPIC,
                producer,
                1,
                10
        ));
    }
}
