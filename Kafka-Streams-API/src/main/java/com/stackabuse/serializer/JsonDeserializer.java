package com.stackabuse.serializer;

import com.google.gson.Gson;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;
import java.util.Objects;

public class JsonDeserializer<T> implements Deserializer<T> {

    private final Gson gson = new Gson();
    private Class<T> deserializedClass;

    public JsonDeserializer(Class<T> deserializedClass) {
        this.deserializedClass = deserializedClass;
    }

    public JsonDeserializer() {
    }

    @Override
    @SuppressWarnings("unchecked")
    public void configure(Map<String, ?> map, boolean b) {
        if(Objects.isNull(deserializedClass)) {
            deserializedClass = (Class<T>) map.get("serializedClass");
        }
    }

    @Override
    public T deserialize(String s, byte[] bytes) {
        if(Objects.isNull(bytes)) {
            return null;
        }
        return gson.fromJson(new String(bytes), deserializedClass);
    }

    @Override
    public void close() {
    }
}
