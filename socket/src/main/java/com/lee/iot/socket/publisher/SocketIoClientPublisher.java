package com.lee.iot.socket.publisher;

import com.lee.iot.socket.model.ClientConnection;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import com.lee.iot.socket.service.ClientSubscriptionRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SocketIoClientPublisher implements RealtimeMessagePublisher {

    private final ClientSubscriptionRegistry subscriptionRegistry;
    private final SocketIoConnectionSender sender;

    @Override
    public void publish(RealtimeMessageEnvelope envelope) {
        for (ClientConnection connection : subscriptionRegistry.subscribers(
                envelope.topic(),
                ClientConnection.Protocol.SOCKET_IO
        )) {
            sender.send(connection, envelope);
        }
    }
}
