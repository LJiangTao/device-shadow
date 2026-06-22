package com.lee.iot.repository;

import com.lee.iot.repository.entity.DeviceShadowOperationPropertyEntity;
import com.lee.iot.repository.enums.device.ShadowOperationPropertyStatus;
import com.lee.iot.repository.id.DeviceShadowOperationPropertyId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DeviceShadowOperationPropertyRepository
        extends JpaRepository<DeviceShadowOperationPropertyEntity, DeviceShadowOperationPropertyId> {

    List<DeviceShadowOperationPropertyEntity> findByOperationOperationId(
            UUID operationId
    );

    List<DeviceShadowOperationPropertyEntity> findByDeviceIdAndIdPropertyKeyOrderByCreatedAtDesc(
            Long deviceId,
            String propertyKey
    );

    List<DeviceShadowOperationPropertyEntity> findByStatus(
            ShadowOperationPropertyStatus status
    );
}