package com.lee.iot.socket.config;

import io.vertx.core.VertxOptions;

public record SocketConfiguration(
        SocketServerProperties server,
        SocketMqttProperties mqtt,
        SocketKafkaProperties kafka
) {

    public static SocketConfiguration load() {
        SocketServerProperties server = new SocketServerProperties();


        server.setHost(value("socket.server.host", "SOCKET_SERVER_HOST", server.getHost()));
        server.setPort(intValue("socket.server.port", "SOCKET_SERVER_PORT", server.getPort()));
        server.setSocketIoPort(intValue("socket.server.socket-io-port", "SOCKET_SERVER_SOCKET_IO_PORT", server.getSocketIoPort()));
        server.setWebsocketPath(value("socket.server.websocket-path", "SOCKET_SERVER_WEBSOCKET_PATH", server.getWebsocketPath()));
        server.setSocketIoPath(value("socket.server.socket-io-path", "SOCKET_SERVER_SOCKET_IO_PATH", server.getSocketIoPath()));

        SocketMqttProperties mqtt = new SocketMqttProperties();
        mqtt.setEnabled(booleanValue("socket.mqtt.enabled", "SOCKET_MQTT_ENABLED", mqtt.isEnabled()));
        mqtt.setBrokerUrl(value("socket.mqtt.broker-url", "SOCKET_MQTT_BROKER_URL", mqtt.getBrokerUrl()));
        mqtt.setClientId(value("socket.mqtt.client-id", "SOCKET_MQTT_CLIENT_ID", mqtt.getClientId()));
        mqtt.setUsername(optionalValue("socket.mqtt.username", "SOCKET_MQTT_USERNAME", mqtt.getUsername()));
        mqtt.setPassword(optionalValue("socket.mqtt.password", "SOCKET_MQTT_PASSWORD", mqtt.getPassword()));
        mqtt.setQos(intValue("socket.mqtt.qos", "SOCKET_MQTT_QOS", mqtt.getQos()));
        mqtt.setRetained(booleanValue("socket.mqtt.retained", "SOCKET_MQTT_RETAINED", mqtt.isRetained()));

        SocketKafkaProperties kafka = new SocketKafkaProperties();
        kafka.setBootstrapServers(value("socket.kafka.bootstrap-servers", "SOCKET_KAFKA_BOOTSTRAP_SERVERS", kafka.getBootstrapServers()));
        kafka.setGroupId(value("socket.kafka.group-id", "SOCKET_KAFKA_GROUP_ID", kafka.getGroupId()));
        kafka.setClientId(value("socket.kafka.client-id", "SOCKET_KAFKA_CLIENT_ID", kafka.getClientId()));
        kafka.setPollDurationMillis(longValue("socket.kafka.poll-duration-millis", "SOCKET_KAFKA_POLL_DURATION_MILLIS", kafka.getPollDurationMillis()));

        return new SocketConfiguration(server, mqtt, kafka);
    }

    private static String value(String propertyName, String environmentName, String defaultValue) {
        String value = optionalValue(propertyName, environmentName, defaultValue);
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String optionalValue(String propertyName, String environmentName, String defaultValue) {
        String property = System.getProperty(propertyName);
        if (property != null) {
            return property;
        }
        String environment = System.getenv(environmentName);
        return environment == null ? defaultValue : environment;
    }

    private static int intValue(String propertyName, String environmentName, int defaultValue) {
        return Integer.parseInt(value(propertyName, environmentName, Integer.toString(defaultValue)));
    }

    private static long longValue(String propertyName, String environmentName, long defaultValue) {
        return Long.parseLong(value(propertyName, environmentName, Long.toString(defaultValue)));
    }

    private static boolean booleanValue(String propertyName, String environmentName, boolean defaultValue) {
        return Boolean.parseBoolean(value(propertyName, environmentName, Boolean.toString(defaultValue)));
    }
}
