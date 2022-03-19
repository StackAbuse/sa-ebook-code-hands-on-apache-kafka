package com.stackabuse;

import com.stackabuse.constants.ApplicationConstants;
import com.stackabuse.joiner.TransactionJoiner;
import com.stackabuse.model.CorrelatedTransaction;
import com.stackabuse.model.RewardAccumulator;
import com.stackabuse.model.Transaction;
import com.stackabuse.model.TransactionPattern;
import com.stackabuse.partitioner.RewardsStreamPartitioner;
import com.stackabuse.producer.FakeDataProducer;
import com.stackabuse.serializer.JsonDeserializer;
import com.stackabuse.serializer.JsonSerializer;
import com.stackabuse.timestamp_extractor.TransactionTimestampExtractor;
import com.stackabuse.transformer.TransactionRewardTransformer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Properties;

public class KafkaStreamsDSLStatefulAPI {

    public static void main(String[] args) throws InterruptedException {
        final Logger logger = LoggerFactory.getLogger(KafkaStreamsDSLStatefulAPI.class);

        // Setting the Properties
        StreamsConfig streamsConfig = new StreamsConfig(setProperties());

        // Serializing and Deserializing the transaction
        JsonDeserializer<Transaction> transactionJsonDeserializer = new JsonDeserializer<>(Transaction.class);
        JsonSerializer<Transaction> transactionJsonSerializer = new JsonSerializer<>();

        // Serializing and Deserializing the rewards
        JsonSerializer<RewardAccumulator> rewardAccumulatorJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<RewardAccumulator> rewardAccumulatorJsonDeserializer = new JsonDeserializer<>(RewardAccumulator.class);

        // Serializing and Deserializing the transaction pattern
        JsonSerializer<TransactionPattern> transactionPatternJsonSerializer = new JsonSerializer<>();
        JsonDeserializer<TransactionPattern> transactionPatternJsonDeserializer = new JsonDeserializer<>(TransactionPattern.class);

        // Generating Serde out of serializers and deserializers
        Serde<RewardAccumulator> rewardAccumulatorSerde = Serdes.serdeFrom(rewardAccumulatorJsonSerializer, rewardAccumulatorJsonDeserializer);
        Serde<TransactionPattern> transactionPatternSerde = Serdes.serdeFrom(transactionPatternJsonSerializer, transactionPatternJsonDeserializer);
        Serde<Transaction> transactionSerde = Serdes.serdeFrom(transactionJsonSerializer, transactionJsonDeserializer);
        Serde<String> stringSerde = Serdes.String();

        // Predicate to branch out transactions to perform Joins
        Predicate<String, Transaction> computerTransaction = (key, transaction) -> transaction.getItemPurchased().contains("Computer");
        Predicate<String, Transaction> bagTransaction = (key, transaction) -> transaction.getItemPurchased().contains("Bag");

        // Initiating the Stream Builder
        StreamsBuilder kStreamBuilder = new StreamsBuilder();

        // Masking the card details in transaction objects
        KStream<String, Transaction> transactionKStream = kStreamBuilder
                .stream( ApplicationConstants.SOURCE_TOPIC, Consumed.with(stringSerde, transactionSerde))
                .mapValues(p -> Transaction.builder(p).maskCard().build());

        // Branch and Join Streams by customerID for a given item category
        final int COMPUTER_TRANSACTION = 0;
        final int BAG_TRANSACTION = 0;
        KStream<String, Transaction>[] branchesStream = transactionKStream
                .selectKey((k, v)-> v.getCustomerId())
                .branch(computerTransaction, bagTransaction);
        KStream<String, Transaction> computerStream = branchesStream[COMPUTER_TRANSACTION];
        KStream<String, Transaction> bagStream = branchesStream[BAG_TRANSACTION];
        ValueJoiner<Transaction, Transaction, CorrelatedTransaction> transactionJoiner = new TransactionJoiner();
        JoinWindows twentyMinuteWindow =  JoinWindows.of(Duration.ofMinutes(60 * (long)1000 * 30));
        KStream<String, CorrelatedTransaction> joinedKStream = computerStream.join(
                bagStream,
                transactionJoiner,
                twentyMinuteWindow,
                StreamJoined.with(stringSerde, transactionSerde, transactionSerde));
        joinedKStream.print(Printed.<String, CorrelatedTransaction>toSysOut().withLabel("joined KStream"));

        // Retrieving a common pattern between transactions and forwarding to patterns topic
        KStream<String, TransactionPattern> patternKStream = transactionKStream
                .mapValues(transaction -> TransactionPattern.builder(transaction).build());
        patternKStream.print(Printed.<String, TransactionPattern>toSysOut().withLabel(ApplicationConstants.SINK1_TOPIC));
        patternKStream.to(ApplicationConstants.SINK1_TOPIC, Produced.with(stringSerde, transactionPatternSerde));

        // adding State to processor
        String rewardsStateStoreName = "rewardsPointsStore";
        RewardsStreamPartitioner streamPartitioner = new RewardsStreamPartitioner();
        KeyValueBytesStoreSupplier storeSupplier = Stores.inMemoryKeyValueStore(rewardsStateStoreName);
        StoreBuilder<KeyValueStore<String, Integer>> storeBuilder = Stores
                .keyValueStoreBuilder(storeSupplier, Serdes.String(), Serdes.Integer());
        kStreamBuilder.addStateStore(storeBuilder);
        KStream<String, Transaction> transByCustomerStream = transactionKStream
                .through( "customer_transactions", Produced.with(stringSerde, transactionSerde, streamPartitioner));

        // Retrieving rewards from transactions and forwarding to rewards topic
        KStream<String, RewardAccumulator> statefulRewardAccumulator = transByCustomerStream
                .transformValues(() ->  new TransactionRewardTransformer(rewardsStateStoreName), rewardsStateStoreName);
        statefulRewardAccumulator.print(Printed.<String, RewardAccumulator>toSysOut().withLabel(ApplicationConstants.SINK2_TOPIC));
        statefulRewardAccumulator.to(ApplicationConstants.SINK2_TOPIC, Produced.with(stringSerde, rewardAccumulatorSerde));

        // Forwarding the masked transaction to transactions topic
        transactionKStream.print(Printed.<String, Transaction>toSysOut().withLabel(ApplicationConstants.SINK3_TOPIC));
        transactionKStream.to(ApplicationConstants.SINK3_TOPIC, Produced.with(stringSerde, transactionSerde));

        // Initiating the Producer to produce transactions data
        logger.info("Producing Transaction Processor Messages to Kafka");
        FakeDataProducer.produceTransactionsData();

        // Managing the Transaction stream and the producer
        logger.info("Starting Transaction Streams Stateful DSL");
        KafkaStreams kafkaStreams = new KafkaStreams(kStreamBuilder.build(), streamsConfig);
        kafkaStreams.start();
        logger.info("Now started Transaction Streams Stateful DSL");
        Thread.sleep(65000);
        logger.info("Shutting down the Kafka Streams Transaction Processor now");
        kafkaStreams.close();
        FakeDataProducer.shutdown();
    }

    private static Properties setProperties() {
        Properties props = new Properties();
        props.put(StreamsConfig.CLIENT_ID_CONFIG, "KafkaStreamsDSLJob");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "streams-transaction");
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-dsl-stateful-api");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "5000");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, ApplicationConstants.BOOTSTRAP_SERVERS);
        props.put(ConsumerConfig.METADATA_MAX_AGE_CONFIG, "10000");
        props.put(StreamsConfig.REPLICATION_FACTOR_CONFIG, 1);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, TransactionTimestampExtractor.class);
        return props;
    }
}
