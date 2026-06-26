package com.lee.iot.socket.service;

import com.lee.iot.socket.model.ClientTopic;
import com.lee.iot.socket.model.KafkaTopic;
import com.lee.iot.socket.model.SocketMessageType;
import com.lee.iot.socket.model.TopicRoute;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TopicRouterTest {

    private final TopicRouter topicRouter = new TopicRouter();

    @Test
    void routesDeviceShadowReportedToSeparateKafkaAndClientTopicModels() {
        TopicRoute route = topicRouter.route(SocketMessageType.DEVICE_SHADOW_REPORTED);

        assertThat(route.messageType()).isEqualTo(SocketMessageType.DEVICE_SHADOW_REPORTED);
        assertThat(route.kafkaTopic()).isEqualTo(new KafkaTopic("device.shadow.reported"));
        assertThat(route.clientTopic()).isEqualTo(new ClientTopic("device.shadow.reported"));
        assertThat(route.kafkaTopic().value()).isEqualTo(route.clientTopic().value());
    }

    @Test
    void rejectsUnknownMessageTypes() {
        assertThatThrownBy(() -> SocketMessageType.from("missing"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown socket message type");
    }
}
