package com.lee.iot.socket.service;

import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import com.lee.iot.socket.publisher.RealtimeMessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RealtimeMessageDispatcher {

    private final List<RealtimeMessagePublisher> publishers;

    public void dispatch(RealtimeMessageEnvelope envelope) {
        for (RealtimeMessagePublisher publisher : publishers) {
            try {
                publisher.publish(envelope);
            } catch (RuntimeException e) {
                log.warn("Realtime publisher failed: {}: {}", publisher.getClass().getSimpleName(), e.toString());
            }
        }
    }
}
