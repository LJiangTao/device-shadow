package com.lee.iot.repository;

import com.lee.iot.repository.entity.DeviceShadowOperationEntity;
import com.lee.iot.repository.enums.device.ShadowOperationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceShadowOperationRepository
        extends JpaRepository<DeviceShadowOperationEntity, UUID> {

    Optional<DeviceShadowOperationEntity> findByDeviceIdAndRequestId(
            Long deviceId,
            String requestId
    );

    List<DeviceShadowOperationEntity> findByDeviceIdOrderByCreatedAtDesc(
            Long deviceId
    );

    List<DeviceShadowOperationEntity> findByStatus(
            ShadowOperationStatus status
    );
}