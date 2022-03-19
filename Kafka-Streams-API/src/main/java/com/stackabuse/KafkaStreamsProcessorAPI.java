package com.stackabuse;

import com.stackabuse.constants.ApplicationConstants;
import com.stackabuse.model.RewardAccumulator;
import com.stackabuse.model.Transaction;
import com.stackabuse.model.TransactionPattern;
import com.stackabuse.processor.CardAnonymizer;
import com.stackabuse.processor.CustomerRewards;
import com.stackabuse.processor.TransactionPatterns;
import com.stackabuse.producer.FakeDataProducer;
import com.stackabuse.serializer.JsonDeserializer;
import com.stackabuse.serializer.JsonSerializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Properties;

public class KafkaStreamsProcessorAPI {

    public static void main(String[] args) throws InterruptedException {
        final Logger logger = LoggerFactory.getLogger(KafkaStreamsProcessorAPI.class);

        // Setting the Properties
        StreamsConfig streamingConfig = new StreamsConfig(setProperties());

        // Serializing and Deserializing the objects
        JsonDeserializer<Transaction> transactionJsonDeserializer = new JsonDeserializer<>(Transaction.class);
        JsonSerializer<Transaction> transactionJsonSerializer = new JsonSerializer<>();
        JsonSerializer<RewardAccumulator> rewardAccumulatorJsonSerializer = new JsonSerializer<>();
        JsonSerializer<TransactionPattern> transactionPatternJsonSerializer = new JsonSerializer<>();
        StringDeserializer stringDeserializer = new StringDeserializer();
        StringSerializer stringSerializer = new StringSerializer();

        // Building the Topology to process the streams
        Topology topologyBuilder = new Topology();
        topologyBuilder
                .addSource("SOURCE", stringDeserializer, transactionJsonDeserializer, ApplicationConstants.SOURCE_TOPIC)
                .addProcessor("PROCESS1", CardAnonymizer::new, "SOURCE")
                .addProcessor("PROCESS2", TransactionPatterns::new, "PROCESS1")
                .addProcessor("PROCESS3", CustomerRewards::new, "PROCESS1")
                .addSink("SINK1", ApplicationConstants.SINK1_TOPIC, stringSerializer, transactionPatternJsonSerializer, "PROCESS2")
                .addSink("SINK2", ApplicationConstants.SINK2_TOPIC,stringSerializer, rewardAccumulatorJsonSerializer, "PROCESS3")
                .addSink("SINK3", ApplicationConstants.SINK3_TOPIC, stringSerializer, transactionJsonSerializer, "PROCESS1");

        // Initiating the Producer to produce transactions data
        logger.info("Producing Transaction Processor Messages to Kafka");
        FakeDataProducer.produceTransactionsData();

        // Managing the Transaction stream and the producer
        logger.info("Starting Kafka Streams Transaction Processor");
        KafkaStreams streaming = new KafkaStreams(topologyBuilder, streamingConfig);
        streaming.start();
        logger.info("Now started Kafka Streams Transaction Processor");
        Thread.sleep(65000);
        logger.info("Shutting down the Kafka Streams Transaction Processor now");
        streaming.close();
        FakeDataProducer.shutdown();
    }

    private static Properties setProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "TransactionProcessorJob");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "transaction-consumer-group");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "transaction-processor-api");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class);
        return props;
    }
}
