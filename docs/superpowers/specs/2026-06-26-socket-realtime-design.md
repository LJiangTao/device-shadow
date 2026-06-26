# Socket Realtime Module Design

## Scope

Add a socket module to the existing `device-shadow` Spring Boot application. The module receives realtime messages from other backend services through HTTP POST, writes them to Kafka by message type, consumes those Kafka messages, and publishes the wrapped realtime message through three delivery channels:

- Built-in WebSocket clients.
- Built-in Socket.IO clients.
- Mosquitto MQTT broker.

Frontend subscription topic values and Kafka topic values are the same for now, but they are represented by separate classes so the browser-facing protocol and Kafka routing model can diverge later.

## Architecture

The module lives under `com.lee.iot.socket` and follows the existing single-module project structure.

- `config`: socket server, Kafka, and MQTT configuration properties.
- `controller`: Spring MVC HTTP POST ingress endpoint.
- `model`: request, envelope, topic, and message type models.
- `service`: topic routing, Kafka ingestion, subscription registry, and dispatch orchestration.
- `publisher`: protocol-specific publishers for WebSocket, Socket.IO, and MQTT.
- `consumer`: Kafka listeners that consume routed realtime topics.
- `verticle`: Vert.x WebSocket runtime and Socket.IO adapter startup.

Spring remains the application container. Vert.x is used for the built-in WebSocket runtime. Socket.IO is implemented through a Socket.IO-compatible Java adapter and wired into the same subscription registry and dispatcher.

## Message Flow

1. Another backend service sends a message to `POST /socket/messages`.
2. The HTTP endpoint validates `messageType` and `payload`.
3. `TopicRouter` maps `messageType` to a `KafkaTopic` and a `ClientTopic`.
4. `SocketMessageIngestService` writes the raw incoming message to the mapped Kafka topic.
5. Kafka consumers consume messages from configured realtime topics.
6. The consumer wraps each consumed record into a realtime envelope.
7. `RealtimeMessageDispatcher` publishes the envelope to all enabled delivery channels:
   - `WebSocketClientPublisher` sends to built-in WebSocket sessions subscribed to the `ClientTopic`.
   - `SocketIoClientPublisher` sends to built-in Socket.IO sessions subscribed to the `ClientTopic`.
   - `MqttClientPublisher` publishes to Mosquitto using the same topic value.

Kafka is the durable internal buffer. WebSocket, Socket.IO, and Mosquitto are parallel fanout channels.

## Protocols

### HTTP Ingress

Endpoint:

```http
POST /socket/messages
```

Request body:

```json
{
  "messageType": "DEVICE_SHADOW_REPORTED",
  "payload": {
    "deviceKey": "device-001",
    "properties": {}
  }
}
```

The endpoint returns the existing `R` response wrapper.

### Client Envelope

Messages delivered to WebSocket, Socket.IO, and Mosquitto use the same JSON shape:

```json
{
  "topic": "device.shadow.reported",
  "messageType": "DEVICE_SHADOW_REPORTED",
  "payload": {},
  "timestamp": "2026-06-26T10:00:00+08:00"
}
```

### WebSocket

Clients connect to:

```text
/ws
```

Subscribe:

```json
{
  "action": "subscribe",
  "topic": "device.shadow.reported"
}
```

Unsubscribe:

```json
{
  "action": "unsubscribe",
  "topic": "device.shadow.reported"
}
```

### Socket.IO

Clients connect to:

```text
/socket.io
```

The Socket.IO adapter accepts subscribe and unsubscribe events using the same payload as WebSocket. Published event payloads use the shared client envelope.

### Mosquitto

The service publishes each client envelope to Mosquitto using the same topic value as `ClientTopic`.

Default broker connection:

```text
tcp://localhost:1883
```

Authentication is not required in the first implementation. Configuration leaves room for username, password, QoS, and retained-message options.

## Topic Model

The first implementation includes one concrete route:

| Message type | Kafka topic | Client topic |
| --- | --- | --- |
| `DEVICE_SHADOW_REPORTED` | `device.shadow.reported` | `device.shadow.reported` |

`KafkaTopic` and `ClientTopic` are separate model classes even when their values are equal. `TopicRoute` stores both together with the `SocketMessageType`.

## Configuration

Default application configuration:

```yaml
socket:
  server:
    host: 0.0.0.0
    port: 8081
    websocket-path: /ws
    socket-io-path: /socket.io
  mqtt:
    enabled: true
    broker-url: tcp://localhost:1883
    client-id: device-shadow-socket
```

Kafka bootstrap and consumer settings continue to use Spring Kafka configuration.

## Error Handling

- Unknown `messageType` returns a failed `R` response and does not write to Kafka.
- Blank topic subscription requests are rejected and do not alter session subscriptions.
- Malformed WebSocket subscription JSON returns an error message to that connection only.
- Kafka consumption failures rely on Spring Kafka error handling defaults in the first implementation.
- MQTT publish failures are logged and do not block WebSocket or Socket.IO delivery.

## Testing

Tests should be added before production code for these behaviors:

- `TopicRouter` maps `DEVICE_SHADOW_REPORTED` to distinct `KafkaTopic` and `ClientTopic` objects with the same value.
- Unknown message types are rejected.
- `ClientSubscriptionRegistry` subscribes, unsubscribes, and removes all subscriptions when a client disconnects.
- `RealtimeMessageDispatcher` calls WebSocket, Socket.IO, and MQTT publishers for a valid envelope.
- HTTP ingress writes to Kafka using the routed Kafka topic.

Integration tests for a real Mosquitto broker are out of scope for the first implementation. MQTT publishing will be covered through a publisher boundary test.
