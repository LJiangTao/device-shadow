package com.lee.iot.socket.model;

public record TopicRoute(SocketMessageType messageType, KafkaTopic kafkaTopic, ClientTopic clientTopic) {
}
