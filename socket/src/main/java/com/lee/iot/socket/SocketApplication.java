package com.lee.iot.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lee.iot.socket.config.SocketConfiguration;
import com.lee.iot.socket.consumer.SocketRealtimeKafkaConsumer;
import com.lee.iot.socket.controller.SocketMessageController;
import com.lee.iot.socket.model.SocketMessageType;
import com.lee.iot.socket.publisher.KafkaSocketMessageProducer;
import com.lee.iot.socket.publisher.MqttClientPublisher;
import com.lee.iot.socket.publisher.RealtimeMessagePublisher;
import com.lee.iot.socket.publisher.SocketIoClientPublisher;
import com.lee.iot.socket.publisher.WebSocketClientPublisher;
import com.lee.iot.socket.service.ClientSubscriptionRegistry;
import com.lee.iot.socket.service.RealtimeMessageDispatcher;
import com.lee.iot.socket.service.SocketMessageIngestService;
import com.lee.iot.socket.service.TopicRouter;
import com.lee.iot.socket.verticle.SocketIoServerLifecycle;
import com.lee.iot.socket.verticle.VertxWebSocketServer;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CountDownLatch;

@Slf4j
public class SocketApplication {

    public static void main(String[] args) throws InterruptedException {
        new SocketApplication().run();
    }

    void run() throws InterruptedException {
        SocketConfiguration configuration = SocketConfiguration.load();
        ObjectMapper objectMapper = objectMapper();
        TopicRouter topicRouter = new TopicRouter();
        ClientSubscriptionRegistry subscriptionRegistry = new ClientSubscriptionRegistry();

        KafkaSocketMessageProducer producer = new KafkaSocketMessageProducer(configuration.kafka(), objectMapper);
        SocketMessageIngestService ingestService = new SocketMessageIngestService(topicRouter, producer);
        SocketMessageController controller = new SocketMessageController(ingestService, objectMapper);

        Vertx vertx = Vertx.vertx();
        VertxWebSocketServer webSocketServer = new VertxWebSocketServer(
                configuration.server(),
                subscriptionRegistry,
                objectMapper,
                controller,
                vertx
        );
        SocketIoServerLifecycle socketIoServer = new SocketIoServerLifecycle(
                configuration.server(),
                subscriptionRegistry,
                objectMapper
        );

        List<RealtimeMessagePublisher> publishers = List.of(
                new WebSocketClientPublisher(subscriptionRegistry, webSocketServer),
                new SocketIoClientPublisher(subscriptionRegistry, socketIoServer),
                new MqttClientPublisher(configuration.mqtt(), objectMapper)
        );
        RealtimeMessageDispatcher dispatcher = new RealtimeMessageDispatcher(publishers);
        SocketRealtimeKafkaConsumer consumer = new SocketRealtimeKafkaConsumer(
                topicRouter,
                dispatcher,
                objectMapper,
                configuration.kafka(),
                List.of(topicRouter.route(SocketMessageType.DEVICE_SHADOW_REPORTED).kafkaTopic())
        );

        CountDownLatch shutdown = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            consumer.close();
            socketIoServer.close();
            producer.close();
            webSocketServer.closeVertx();
            shutdown.countDown();
        }, "socket-shutdown"));

        webSocketServer.start()
                .onSuccess(ignored -> {
                    socketIoServer.start();
                    consumer.start();
                    log.info("Socket application started");
                })
                .onFailure(error -> {
                    log.error("Socket application failed to start", error);
                    shutdown.countDown();
                });
        shutdown.await();
    }

    private static ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
