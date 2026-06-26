package com.lee.iot.socket.verticle;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.iot.socket.config.SocketServerProperties;
import com.lee.iot.socket.model.ClientConnection;
import com.lee.iot.socket.model.ClientSubscriptionMessage;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import com.lee.iot.socket.publisher.SocketIoConnectionSender;
import com.lee.iot.socket.service.ClientSubscriptionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
public class SocketIoServerLifecycle implements SocketIoConnectionSender, AutoCloseable {

    private final SocketServerProperties properties;
    private final ClientSubscriptionRegistry subscriptionRegistry;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<ClientConnection, com.corundumstudio.socketio.SocketIOClient> sessions = new ConcurrentHashMap<>();

    private SocketIOServer server;
    private volatile boolean running;

    public void start() {
        Configuration configuration = new Configuration();
        configuration.setHostname(properties.getHost());
        configuration.setPort(properties.getSocketIoPort());
        configuration.setContext(properties.getSocketIoPath());
        server = new SocketIOServer(configuration);

        server.addConnectListener(client -> {
            ClientConnection connection = new ClientConnection(client.getSessionId().toString(), ClientConnection.Protocol.SOCKET_IO);
            sessions.put(connection, client);
        });
        server.addDisconnectListener(client -> disconnect(new ClientConnection(client.getSessionId().toString(), ClientConnection.Protocol.SOCKET_IO)));
        server.addEventListener("subscribe", ClientSubscriptionMessage.class, (client, message, ackSender) -> {
            ClientConnection connection = new ClientConnection(client.getSessionId().toString(), ClientConnection.Protocol.SOCKET_IO);
            subscriptionRegistry.subscribe(connection, message.clientTopic());
        });
        server.addEventListener("unsubscribe", ClientSubscriptionMessage.class, (client, message, ackSender) -> {
            ClientConnection connection = new ClientConnection(client.getSessionId().toString(), ClientConnection.Protocol.SOCKET_IO);
            subscriptionRegistry.unsubscribe(connection, message.clientTopic());
        });

        server.start();
        running = true;
        log.info("Socket.IO server started on {}:{}{}", properties.getHost(), properties.getSocketIoPort(), properties.getSocketIoPath());
    }

    private void disconnect(ClientConnection connection) {
        sessions.remove(connection);
        subscriptionRegistry.disconnect(connection);
    }

    @Override
    public void send(ClientConnection connection, RealtimeMessageEnvelope envelope) {
        com.corundumstudio.socketio.SocketIOClient client = sessions.get(connection);
        if (client == null) {
            disconnect(connection);
            return;
        }
        client.sendEvent("message", envelope);
    }

    public void stop() {
        running = false;
        if (server != null) {
            server.stop();
        }
        sessions.keySet().forEach(subscriptionRegistry::disconnect);
        sessions.clear();
    }

    @Override
    public void close() {
        stop();
    }

    public boolean isRunning() {
        return running;
    }
}
