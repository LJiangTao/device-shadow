package com.lee.iot.socket.service;

import com.lee.iot.socket.model.ClientConnection;
import com.lee.iot.socket.model.ClientTopic;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class ClientSubscriptionRegistry {

    private final ConcurrentMap<ClientTopic, Set<ClientConnection>> subscribersByTopic = new ConcurrentHashMap<>();
    private final ConcurrentMap<ClientConnection, Set<ClientTopic>> topicsByConnection = new ConcurrentHashMap<>();

    public void subscribe(ClientConnection connection, ClientTopic topic) {
        subscribersByTopic.computeIfAbsent(topic, ignored -> ConcurrentHashMap.newKeySet()).add(connection);
        topicsByConnection.computeIfAbsent(connection, ignored -> ConcurrentHashMap.newKeySet()).add(topic);
    }

    public void unsubscribe(ClientConnection connection, ClientTopic topic) {
        Set<ClientConnection> subscribers = subscribersByTopic.get(topic);
        if (subscribers != null) {
            subscribers.remove(connection);
            if (subscribers.isEmpty()) {
                subscribersByTopic.remove(topic, subscribers);
            }
        }

        Set<ClientTopic> topics = topicsByConnection.get(connection);
        if (topics != null) {
            topics.remove(topic);
            if (topics.isEmpty()) {
                topicsByConnection.remove(connection, topics);
            }
        }
    }

    public void disconnect(ClientConnection connection) {
        Set<ClientTopic> topics = topicsByConnection.remove(connection);
        if (topics == null) {
            return;
        }
        for (ClientTopic topic : topics) {
            Set<ClientConnection> subscribers = subscribersByTopic.get(topic);
            if (subscribers != null) {
                subscribers.remove(connection);
                if (subscribers.isEmpty()) {
                    subscribersByTopic.remove(topic, subscribers);
                }
            }
        }
    }

    public Set<ClientConnection> subscribers(ClientTopic topic, ClientConnection.Protocol protocol) {
        Set<ClientConnection> subscribers = subscribersByTopic.getOrDefault(topic, Collections.emptySet());
        return subscribers.stream()
                .filter(connection -> connection.protocol() == protocol)
                .collect(Collectors.toUnmodifiableSet());
    }
}
