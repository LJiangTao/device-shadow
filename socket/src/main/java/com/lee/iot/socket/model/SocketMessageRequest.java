package com.lee.iot.socket.model;

import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;

public record SocketMessageRequest(
        String messageType,
        Object payload
)  implements Validation {


    public static final SocketMessageRequest INSTANCE = new SocketMessageRequest(null, null);



    @Override
    public ObjectSchemaBuilder schemaRequestPayloadBuilder() {
        return
    }


}
