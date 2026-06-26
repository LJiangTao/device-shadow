package com.lee.iot.socket.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lee.iot.socket.model.SocketMessageRequest;
import com.lee.iot.socket.model.SocketResponse;
import com.lee.iot.socket.service.SocketMessageIngestService;
import lombok.RequiredArgsConstructor;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

@RequiredArgsConstructor
public class SocketMessageController implements Handler<RoutingContext> {

    private final SocketMessageIngestService ingestService;
    private final ObjectMapper objectMapper;

    @Override
    public void handle(RoutingContext context) {
        try {
            SocketMessageRequest request = objectMapper.readValue(context.body().asString(), SocketMessageRequest.class);
            ingestService.ingest(request);
            writeJson(context, 200, SocketResponse.ok(null));
        } catch (IllegalArgumentException e) {
            writeJson(context, 400, SocketResponse.fail("400", e.getMessage()));
        } catch (JsonProcessingException e) {
            writeJson(context, 400, SocketResponse.fail("400", "Invalid request body"));
        } catch (RuntimeException e) {
            writeJson(context, 500, SocketResponse.fail("500", "Failed to ingest socket message"));
        }
    }

    private void writeJson(RoutingContext context, int statusCode, SocketResponse<Void> response) {
        try {
            context.response()
                    .setStatusCode(statusCode)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end(objectMapper.writeValueAsString(response));
        } catch (JsonProcessingException e) {
            context.response()
                    .setStatusCode(500)
                    .putHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .end("{\"success\":false,\"message\":\"Failed to serialize response\",\"data\":null}");
        }
    }
}
