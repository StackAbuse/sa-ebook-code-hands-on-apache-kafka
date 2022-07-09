package com.stackabuse.springcloudkafkastreams.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackabuse.springcloudkafkastreams.model.Transaction;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class TransactionDeserializer  implements Deserializer<Transaction> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Transaction deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data), Transaction.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
