package com.lee.iot.socket.model;

import java.time.OffsetDateTime;

public record RealtimeMessageEnvelope(
        ClientTopic topic,
        SocketMessageType messageType,
        Object payload,
        OffsetDateTime timestamp
) {
}
