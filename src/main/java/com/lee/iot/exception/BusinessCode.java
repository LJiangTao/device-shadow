package com.lee.iot.exception;

import org.springframework.http.HttpStatus;

public interface BusinessCode {


    default HttpStatus getRespStatus() {
        return HttpStatus.BAD_REQUEST;
    }

    String getCode();

    String getMessage();

}
