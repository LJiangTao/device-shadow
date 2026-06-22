package com.lee.iot.repository.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Embeddable
public class DeviceShadowOperationPropertyId implements Serializable {

    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Column(name = "property_key", nullable = false, length = 128)
    private String propertyKey;
}