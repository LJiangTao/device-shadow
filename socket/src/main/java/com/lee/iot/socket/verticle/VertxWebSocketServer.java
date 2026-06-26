package com.lee.iot.socket.verticle;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.iot.socket.config.SocketServerProperties;
import com.lee.iot.socket.controller.RequestValidationHandlers;
import com.lee.iot.socket.controller.SocketMessageController;
import com.lee.iot.socket.controller.SocketMessageValidationSchemas;
import com.lee.iot.socket.model.ClientConnection;
import com.lee.iot.socket.model.ClientSubscriptionMessage;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import com.lee.iot.socket.publisher.WebSocketConnectionSender;
import com.lee.iot.socket.service.ClientSubscriptionRegistry;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.ServerWebSocket;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RequiredArgsConstructor
public class VertxWebSocketServer implements WebSocketConnectionSender, AutoCloseable {

    private final SocketServerProperties properties;
    private final ClientSubscriptionRegistry subscriptionRegistry;
    private final ObjectMapper objectMapper;
    private final SocketMessageController messageController;
    private final Vertx vertx;
    private final ConcurrentMap<ClientConnection, ServerWebSocket> sessions = new ConcurrentHashMap<>();

    private HttpServer server;
    private volatile boolean running;

    public Future<HttpServer> start() {
        Router router = Router.router(vertx);

        router.post("/socket/messages")
                .handler(BodyHandler.create())
                .handler(RequestValidationHandlers.jsonBody(SocketMessageValidationSchemas.socketMessageRequestBody()))
                .handler(messageController)
                .failureHandler(RequestValidationHandlers.badRequestHandler(objectMapper));

        server = vertx.createHttpServer()
                .webSocketHandler(this::handleWebSocket)
                .requestHandler(router);
        return server.listen(properties.getPort(), properties.getHost())
                .onSuccess(ignored -> {
                    running = true;
                    log.info("Vert.x HTTP server started on {}:{}", properties.getHost(), properties.getPort());
                    log.info("Vert.x WebSocket endpoint ready at {}", properties.getWebsocketPath());
                })
                .onFailure(error -> log.error("Failed to start Vert.x HTTP server", error));
    }

    private void handleWebSocket(ServerWebSocket webSocket) {
        if (!properties.getWebsocketPath().equals(webSocket.path())) {
            webSocket.close();
            return;
        }

        ClientConnection connection = new ClientConnection(UUID.randomUUID().toString(), ClientConnection.Protocol.WEBSOCKET);
        sessions.put(connection, webSocket);
        webSocket.textMessageHandler(message -> handleMessage(connection, webSocket, message));
        webSocket.closeHandler(ignored -> disconnect(connection));
        webSocket.exceptionHandler(error -> disconnect(connection));
    }

    private void handleMessage(ClientConnection connection, ServerWebSocket webSocket, String message) {
        try {
            ClientSubscriptionMessage subscription = objectMapper.readValue(message, ClientSubscriptionMessage.class);
            if (subscription.isSubscribe()) {
                subscriptionRegistry.subscribe(connection, subscription.clientTopic());
                return;
            }
            if (subscription.isUnsubscribe()) {
                subscriptionRegistry.unsubscribe(connection, subscription.clientTopic());
                return;
            }
            sendError(webSocket, "Unsupported subscription action");
        } catch (RuntimeException | JsonProcessingException e) {
            sendError(webSocket, "Invalid subscription message");
        }
    }

    private void sendError(ServerWebSocket webSocket, String message) {
        try {
            webSocket.writeTextMessage(objectMapper.writeValueAsString(Map.of("error", message)));
        } catch (JsonProcessingException e) {
            webSocket.writeTextMessage("{\"error\":\"" + message + "\"}");
        }
    }

    private void disconnect(ClientConnection connection) {
        sessions.remove(connection);
        subscriptionRegistry.disconnect(connection);
    }

    @Override
    public void send(ClientConnection connection, RealtimeMessageEnvelope envelope) {
        ServerWebSocket webSocket = sessions.get(connection);
        if (webSocket == null || webSocket.isClosed()) {
            disconnect(connection);
            return;
        }
        try {
            webSocket.writeTextMessage(objectMapper.writeValueAsString(envelope));
        } catch (JsonProcessingException e) {
            log.warn("Failed to serialize WebSocket envelope", e);
        }
    }

    public Future<Void> stop() {
        running = false;
        Future<Void> serverClose = server == null ? Future.succeededFuture() : server.close();
        sessions.keySet().forEach(subscriptionRegistry::disconnect);
        sessions.clear();
        return serverClose;
    }

    @Override
    public void close() {
        stop();
    }

    public Future<Void> closeVertx() {
        if (server != null) {
            server.close();
        }
        if (vertx != null) {
            return vertx.close();
        }
        return Future.succeededFuture();
    }

    public boolean isRunning() {
        return running;
    }
}
