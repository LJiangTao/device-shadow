package com.lee.iot.socket.service;

import com.lee.iot.socket.model.SocketMessageRequest;
import com.lee.iot.socket.model.TopicRoute;
import com.lee.iot.socket.publisher.SocketMessageProducer;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SocketMessageIngestService {

    private final TopicRouter topicRouter;
    private final SocketMessageProducer producer;

    public void ingest(SocketMessageRequest request) {
        TopicRoute route = topicRouter.route(request.messageType());
        producer.send(route.kafkaTopic().value(), request);
    }
}
