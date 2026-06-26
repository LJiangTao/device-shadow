package com.lee.iot.socket.model;

import io.vertx.json.schema.common.dsl.ObjectSchemaBuilder;

public interface Validation {


    ObjectSchemaBuilder schemaRequestPayloadBuilder();

}
