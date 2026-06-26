package com.lee.iot.socket.logging;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;

public class ProcessIdConverter extends ClassicConverter {

    @Override
    public String convert(ILoggingEvent event) {
        return Long.toString(ProcessHandle.current().pid());
    }
}
