package com.stackabuse.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackabuse.model.Notification;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

public class NotificationDeserializer implements Deserializer<Notification> {

    private static final Logger logger = LoggerFactory.getLogger(NotificationDeserializer.class);

    @Override
    public Notification deserialize(String topic, byte[] data) {
        try {
            if (Objects.isNull(data)) {
                logger.error("Received null while deserializing byte[]");
                return null;
            }
            return Notification.fromJson(new String(data, StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new SerializationException("Error when deserializing byte[] to Notification");
        }
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
    }

    @Override
    public void close() {
    }
}
