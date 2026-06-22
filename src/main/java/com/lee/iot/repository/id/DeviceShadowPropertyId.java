package com.lee.iot.repository.id;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DeviceShadowPropertyId implements Serializable {

    private Long deviceId;

    private String propertyKey;
}