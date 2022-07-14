package com.stackabuse.kafkaspringbootreactive.model;

public enum OrderStatus {
    PLACED,
    AVAILABLE,
    UNAVAILABLE,
    READY_TO_PICK,
    PACKED,
    COURIER_BOOKED,
    READY_FOR_MANIFESTATION,
    SHIPPED,
    CANCELLED,
    RETURNED;
}
