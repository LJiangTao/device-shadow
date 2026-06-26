package com.lee.iot.socket.service;

import com.lee.iot.socket.model.ClientTopic;
import com.lee.iot.socket.model.KafkaTopic;
import com.lee.iot.socket.model.SocketMessageType;
import com.lee.iot.socket.model.TopicRoute;

import java.util.Map;

public class TopicRouter {

    private final Map<SocketMessageType, TopicRoute> routes = Map.of(
            SocketMessageType.DEVICE_SHADOW_REPORTED,
            new TopicRoute(
                    SocketMessageType.DEVICE_SHADOW_REPORTED,
                    new KafkaTopic("device.shadow.reported"),
                    new ClientTopic("device.shadow.reported")
            )
    );

    public TopicRoute route(SocketMessageType messageType) {
        TopicRoute route = routes.get(messageType);
        if (route == null) {
            throw new IllegalArgumentException("Unknown socket message type: " + messageType);
        }
        return route;
    }

    public TopicRoute route(String messageType) {
        return route(SocketMessageType.from(messageType));
    }
}
