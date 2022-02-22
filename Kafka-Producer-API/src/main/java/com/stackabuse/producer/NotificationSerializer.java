package com.stackabuse.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackabuse.model.Notification;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.Objects;

public class NotificationSerializer implements Serializer<Notification> {

    private static final Logger logger = LoggerFactory.getLogger(NotificationSerializer.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, Notification data) {
        try {
            if (Objects.isNull(data)){
                logger.error("Received null value while serializing");
                return new byte[0];
            }
            return objectMapper.writeValueAsBytes(data);
        } catch (Exception e) {
            throw new SerializationException("Error when serializing Notification to byte[]");
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }
}
