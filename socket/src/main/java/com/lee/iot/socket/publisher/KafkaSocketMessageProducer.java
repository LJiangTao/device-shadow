package com.lee.iot.socket.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.iot.socket.config.SocketKafkaProperties;
import com.lee.iot.socket.model.SocketMessageRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class KafkaSocketMessageProducer implements SocketMessageProducer, AutoCloseable {

    private final Producer<String, String> producer;
    private final ObjectMapper objectMapper;

    public KafkaSocketMessageProducer(SocketKafkaProperties properties, ObjectMapper objectMapper) {
        this(new KafkaProducer<>(producerProperties(properties)), objectMapper);
    }

    KafkaSocketMessageProducer(Producer<String, String> producer, ObjectMapper objectMapper) {
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @Override
    public void send(String topic, SocketMessageRequest request) {
        try {
            producer.send(new ProducerRecord<>(topic, objectMapper.writeValueAsString(request)), (metadata, error) -> {
                if (error != null) {
                    log.warn("Failed to produce socket message to Kafka topic {}", topic, error);
                }
            });
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Failed to serialize socket message", e);
        }
    }

    @Override
    public void close() {
        producer.close();
    }

    private static Properties producerProperties(SocketKafkaProperties properties) {
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, properties.getBootstrapServers());
        producerProperties.put(ProducerConfig.CLIENT_ID_CONFIG, properties.getClientId() + "-producer");
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return producerProperties;
    }
}
