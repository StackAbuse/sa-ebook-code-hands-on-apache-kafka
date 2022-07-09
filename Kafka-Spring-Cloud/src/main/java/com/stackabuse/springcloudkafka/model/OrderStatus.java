package com.stackabuse.springcloudkafka.model;

public enum OrderStatus {
    PLACED,
    AVAILABLE,
    UNAVAILABLE,
    PICKED,
    PACKED,
    COURIER_BOOKED,
    READY_FOR_MANIFESTATION,
    SHIPPED,
    CANCELLED,
    RETURNED;
}
