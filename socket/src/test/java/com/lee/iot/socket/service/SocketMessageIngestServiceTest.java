package com.lee.iot.socket.service;

import com.lee.iot.socket.model.SocketMessageRequest;
import com.lee.iot.socket.publisher.SocketMessageProducer;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class SocketMessageIngestServiceTest {

    private final SocketMessageProducer producer = mock(SocketMessageProducer.class);
    private final SocketMessageIngestService service = new SocketMessageIngestService(new TopicRouter(), producer);

    @Test
    void sendsRequestToKafkaUsingRoutedTopic() {
        SocketMessageRequest request = new SocketMessageRequest(
                "DEVICE_SHADOW_REPORTED",
                Map.of("deviceKey", "device-001")
        );

        service.ingest(request);

        verify(producer).send("device.shadow.reported", request);
    }

    @Test
    void rejectsUnknownMessageType() {
        SocketMessageRequest request = new SocketMessageRequest("missing", Map.of());

        assertThatThrownBy(() -> service.ingest(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown socket message type");
    }
}
