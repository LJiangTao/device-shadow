package com.lee.iot.exception;

import org.springframework.http.HttpStatus;

public enum DeviceCode implements BusinessCode {

    DEVICE_NOT_EXISTS("device-not-exists", "device.not.exists"),
    DEVICE_SHADOW_INVALID_REQUEST("device-shadow-invalid-request", "device.shadow.invalid.request"),
    DEVICE_SHADOW_VERSION_CONFLICT("device-shadow-version-conflict", "device.shadow.version.conflict"),


    ;


    private final HttpStatus respStatus;

    private final String code;

    private final String message;

    DeviceCode(HttpStatus respStatus, String code, String message) {
        this.respStatus = respStatus;
        this.code = code;
        this.message = message;
    }

    DeviceCode(String code, String message) {
        this.respStatus = HttpStatus.BAD_REQUEST;
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
