package com.lee.iot.socket.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.iot.socket.config.SocketMqttProperties;
import com.lee.iot.socket.model.RealtimeMessageEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

@Slf4j
@RequiredArgsConstructor
public class MqttClientPublisher implements RealtimeMessagePublisher {

    private final SocketMqttProperties properties;
    private final ObjectMapper objectMapper;

    private MqttClient client;

    @Override
    public synchronized void publish(RealtimeMessageEnvelope envelope) {
        if (!properties.isEnabled()) {
            return;
        }

        try {
            MqttClient mqttClient = mqttClient();
            byte[] payload = objectMapper.writeValueAsBytes(envelope);
            MqttMessage message = new MqttMessage(payload);
            message.setQos(properties.getQos());
            message.setRetained(properties.isRetained());
            mqttClient.publish(envelope.topic().value(), message);
        } catch (MqttException | JsonProcessingException e) {
            log.warn("Failed to publish realtime message to MQTT topic {}", envelope.topic().value(), e);
        }
    }

    private MqttClient mqttClient() throws MqttException {
        if (client == null) {
            client = new MqttClient(properties.getBrokerUrl(), properties.getClientId(), new MemoryPersistence());
        }
        if (!client.isConnected()) {
            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            if (properties.getUsername() != null && !properties.getUsername().isBlank()) {
                options.setUserName(properties.getUsername());
            }
            if (properties.getPassword() != null) {
                options.setPassword(properties.getPassword().toCharArray());
            }
            client.connect(options);
        }
        return client;
    }
}
