package com.lee.iot.socket.service;

import com.lee.iot.socket.model.ClientTopic;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import com.lee.iot.socket.model.SocketMessageType;
import com.lee.iot.socket.publisher.RealtimeMessagePublisher;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;

class RealtimeMessageDispatcherTest {

    @Test
    void dispatchesEnvelopeToEveryPublisher() {
        RealtimeMessagePublisher websocket = mock(RealtimeMessagePublisher.class);
        RealtimeMessagePublisher socketIo = mock(RealtimeMessagePublisher.class);
        RealtimeMessagePublisher mqtt = mock(RealtimeMessagePublisher.class);
        RealtimeMessageEnvelope envelope = envelope();

        new RealtimeMessageDispatcher(List.of(websocket, socketIo, mqtt)).dispatch(envelope);

        verify(websocket).publish(envelope);
        verify(socketIo).publish(envelope);
        verify(mqtt).publish(envelope);
    }

    @Test
    void continuesDispatchingWhenOnePublisherFails() {
        RealtimeMessagePublisher failing = mock(RealtimeMessagePublisher.class);
        RealtimeMessagePublisher remaining = mock(RealtimeMessagePublisher.class);
        RealtimeMessageEnvelope envelope = envelope();
        doThrow(new IllegalStateException("publish failed")).when(failing).publish(envelope);

        new RealtimeMessageDispatcher(List.of(failing, remaining)).dispatch(envelope);

        verify(failing, times(1)).publish(envelope);
        verify(remaining, times(1)).publish(envelope);
    }

    private RealtimeMessageEnvelope envelope() {
        return new RealtimeMessageEnvelope(
                new ClientTopic("device.shadow.reported"),
                SocketMessageType.DEVICE_SHADOW_REPORTED,
                Map.of("deviceKey", "device-001"),
                OffsetDateTime.parse("2026-06-26T10:00:00+08:00")
        );
    }
}
