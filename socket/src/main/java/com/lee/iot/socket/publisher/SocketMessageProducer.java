package com.lee.iot.socket.publisher;

import com.lee.iot.socket.model.SocketMessageRequest;

public interface SocketMessageProducer {

    void send(String topic, SocketMessageRequest request);
}
