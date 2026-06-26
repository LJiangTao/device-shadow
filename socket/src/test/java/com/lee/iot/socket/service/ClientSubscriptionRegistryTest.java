package com.lee.iot.socket.service;

import com.lee.iot.socket.model.ClientConnection;
import com.lee.iot.socket.model.ClientTopic;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClientSubscriptionRegistryTest {

    private final ClientSubscriptionRegistry registry = new ClientSubscriptionRegistry();

    @Test
    void subscribesConnectionToTopic() {
        ClientConnection connection = new ClientConnection("ws-1", ClientConnection.Protocol.WEBSOCKET);
        ClientTopic topic = new ClientTopic("device.shadow.reported");

        registry.subscribe(connection, topic);

        assertThat(registry.subscribers(topic, ClientConnection.Protocol.WEBSOCKET))
                .containsExactly(connection);
    }

    @Test
    void unsubscribesConnectionFromTopic() {
        ClientConnection connection = new ClientConnection("ws-1", ClientConnection.Protocol.WEBSOCKET);
        ClientTopic topic = new ClientTopic("device.shadow.reported");

        registry.subscribe(connection, topic);
        registry.unsubscribe(connection, topic);

        assertThat(registry.subscribers(topic, ClientConnection.Protocol.WEBSOCKET)).isEmpty();
    }

    @Test
    void removesConnectionFromAllTopicsWhenDisconnected() {
        ClientConnection connection = new ClientConnection("socket-1", ClientConnection.Protocol.SOCKET_IO);
        ClientTopic reported = new ClientTopic("device.shadow.reported");
        ClientTopic desired = new ClientTopic("device.shadow.desired");

        registry.subscribe(connection, reported);
        registry.subscribe(connection, desired);
        registry.disconnect(connection);

        assertThat(registry.subscribers(reported, ClientConnection.Protocol.SOCKET_IO)).isEmpty();
        assertThat(registry.subscribers(desired, ClientConnection.Protocol.SOCKET_IO)).isEmpty();
    }
}
