package com.lee.iot.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DeviceException extends RuntimeException
{

    private final HttpStatus returnStatus;

    private final String errorCode;

    private final String errorMsg;


    public DeviceException(HttpStatus returnStatus, String errorCode, String errorMsg) {
        this.returnStatus = returnStatus;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }


    public DeviceException(String errorCode, String errorMsg) {
        this(HttpStatus.OK, errorCode, errorMsg);
    }


}
