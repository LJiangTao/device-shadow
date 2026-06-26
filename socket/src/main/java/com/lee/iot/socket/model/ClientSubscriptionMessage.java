package com.lee.iot.socket.model;

public record ClientSubscriptionMessage(String action, String topic) {

    public boolean isSubscribe() {
        return "subscribe".equalsIgnoreCase(action);
    }

    public boolean isUnsubscribe() {
        return "unsubscribe".equalsIgnoreCase(action);
    }

    public ClientTopic clientTopic() {
        return new ClientTopic(topic);
    }
}
