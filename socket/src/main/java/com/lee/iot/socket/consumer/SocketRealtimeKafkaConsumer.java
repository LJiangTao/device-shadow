package com.lee.iot.socket.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.iot.socket.config.SocketKafkaProperties;
import com.lee.iot.socket.model.KafkaTopic;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import com.lee.iot.socket.model.SocketMessageRequest;
import com.lee.iot.socket.model.SocketMessageType;
import com.lee.iot.socket.model.TopicRoute;
import com.lee.iot.socket.service.RealtimeMessageDispatcher;
import com.lee.iot.socket.service.TopicRouter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@RequiredArgsConstructor
public class SocketRealtimeKafkaConsumer implements AutoCloseable {

    private final TopicRouter topicRouter;
    private final RealtimeMessageDispatcher dispatcher;
    private final ObjectMapper objectMapper;
    private final Consumer<String, String> consumer;
    private final Duration pollDuration;
    private final AtomicBoolean running = new AtomicBoolean();
    private Thread worker;

    public SocketRealtimeKafkaConsumer(
            TopicRouter topicRouter,
            RealtimeMessageDispatcher dispatcher,
            ObjectMapper objectMapper,
            SocketKafkaProperties properties,
            List<KafkaTopic> topics
    ) {
        this(
                topicRouter,
                dispatcher,
                objectMapper,
                new KafkaConsumer<>(consumerProperties(properties)),
                Duration.ofMillis(properties.getPollDurationMillis())
        );
        consumer.subscribe(topics.stream().map(KafkaTopic::value).toList());
    }

    public void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }
        worker = new Thread(this::pollLoop, "socket-realtime-kafka-consumer");
        worker.start();
    }

    private void pollLoop() {
        try {
            while (running.get()) {
                try {
                for (ConsumerRecord<String, String> record : consumer.poll(pollDuration)) {
                    consume(objectMapper.readValue(record.value(), SocketMessageRequest.class));
                }
                } catch (RuntimeException e) {
                    if (running.get()) {
                        log.warn("Kafka realtime consumer failed while polling", e);
                    }
                } catch (Exception e) {
                    log.warn("Kafka realtime consumer failed to decode message", e);
                }
            }
        } finally {
            consumer.close();
        }
    }

    void consume(SocketMessageRequest request) {
        SocketMessageType messageType = SocketMessageType.from(request.messageType());
        TopicRoute route = topicRouter.route(messageType);
        dispatcher.dispatch(new RealtimeMessageEnvelope(
                route.clientTopic(),
                messageType,
                request.payload(),
                OffsetDateTime.now()
        ));
    }

    @Override
    public void close() {
        running.set(false);
        if (consumer != null) {
            consumer.wakeup();
        }
        if (worker != null) {
            worker.interrupt();
        }
    }

    private static Properties consumerProperties(SocketKafkaProperties properties) {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, properties.getGroupId());
        consumerProperties.put(ConsumerConfig.CLIENT_ID_CONFIG, properties.getClientId() + "-consumer");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return consumerProperties;
    }
}
