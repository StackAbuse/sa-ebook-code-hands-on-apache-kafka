package com.stackabuse.springcloudkafkastreams.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackabuse.springcloudkafkastreams.model.Order;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class OrderDeserializer implements Deserializer<Order> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Order deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data), Order.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
