package com.lee.iot.util.web;


import lombok.Getter;

@Getter
public class R<T> {

    private final String code;

    private final String message;

    private final T data;

    public R(String code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> R<T> ok(T data) {
        return new R<T>("200", null, data);
    }

    public static <T> R<T> fail(String code, String message) {
        return new R<T>(code, message, null);
    }
}
