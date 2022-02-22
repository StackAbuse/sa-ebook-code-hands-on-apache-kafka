package com.stackabuse.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public class Notification {

    private final String date;
    private final String orderId;
    private final String message;
    private final String userId;
    private final String name;
    private final String email;

    public Notification(String date, String orderId, String message, String userId, String name, String email) {
        this.date = date;
        this.orderId = orderId;
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    public Notification() {
        date = null;
        orderId = null;
        message = null;
        userId = null;
        name = null;
        email = null;
    }

    public String getDate() {
        return date;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (Objects.isNull(o) || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(orderId, that.orderId)
                && Objects.equals(userId, that.userId)
                && Objects.equals(date, that.date)
                && Objects.equals(message, that.message)
                && Objects.equals(name, that.name)
                && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, orderId, message, userId, name, email);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "date=" + date +
                ", orderId=" + orderId +
                ", message='" + message + '\'' +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(new Notification(date, orderId, message, userId, name, email));
    }

    public static Notification fromJson(final String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, Notification.class);
    }
}
