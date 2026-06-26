package com.lee.iot.socket.model;

import java.util.Arrays;

public enum SocketMessageType {

    DEVICE_SHADOW_REPORTED;

    public static SocketMessageType from(String value) {
        return Arrays.stream(values())
                .filter(type -> type.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown socket message type: " + value));
    }
}
