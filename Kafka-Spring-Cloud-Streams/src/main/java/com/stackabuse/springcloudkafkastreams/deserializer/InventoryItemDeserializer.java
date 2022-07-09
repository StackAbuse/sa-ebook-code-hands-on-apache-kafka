package com.stackabuse.springcloudkafkastreams.deserializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackabuse.springcloudkafkastreams.model.InventoryItem;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class InventoryItemDeserializer implements Deserializer<InventoryItem> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public InventoryItem deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(new String(data), InventoryItem.class);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
    }
}
