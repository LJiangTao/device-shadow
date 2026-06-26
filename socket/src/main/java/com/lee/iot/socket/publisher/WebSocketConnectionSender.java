package com.lee.iot.socket.publisher;

import com.lee.iot.socket.model.ClientConnection;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;

public interface WebSocketConnectionSender {

    void send(ClientConnection connection, RealtimeMessageEnvelope envelope);
}
