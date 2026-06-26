package com.lee.iot.socket.model;

public record KafkaTopic(String value) {

    public KafkaTopic {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Kafka topic must not be blank");
        }
    }
}
