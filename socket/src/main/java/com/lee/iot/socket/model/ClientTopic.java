package com.lee.iot.socket.model;

public record ClientTopic(String value) {

    public ClientTopic {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Client topic must not be blank");
        }
    }
}
