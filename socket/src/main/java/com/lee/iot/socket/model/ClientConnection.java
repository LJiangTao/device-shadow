package com.lee.iot.socket.model;

public record ClientConnection(String id, Protocol protocol) {

    public ClientConnection {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Client connection id must not be blank");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("Client connection protocol must not be null");
        }
    }

    public enum Protocol {
        WEBSOCKET,
        SOCKET_IO
    }
}
