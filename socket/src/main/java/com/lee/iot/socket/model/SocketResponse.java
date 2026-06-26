package com.lee.iot.socket.model;

public record SocketResponse<T>(String code, String message, T data) {

    public static <T> SocketResponse<T> ok(T data) {
        return new SocketResponse<>("200", null, data);
    }

    public static <T> SocketResponse<T> fail(String code, String message) {
        return new SocketResponse<>(code, message, null);
    }
}
