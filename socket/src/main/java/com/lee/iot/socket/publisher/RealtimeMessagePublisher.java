package com.lee.iot.socket.publisher;

import com.lee.iot.socket.model.RealtimeMessageEnvelope;

public interface RealtimeMessagePublisher {

    void publish(RealtimeMessageEnvelope envelope);
}
