package com.lee.iot.socket.config;

public class SocketKafkaProperties {

    private String bootstrapServers = "localhost:9092";

    private String groupId = "device-shadow-socket-realtime";

    private String clientId = "device-shadow-socket";

    private long pollDurationMillis = 1000;

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public long getPollDurationMillis() {
        return pollDurationMillis;
    }

    public void setPollDurationMillis(long pollDurationMillis) {
        this.pollDurationMillis = pollDurationMillis;
    }
}
