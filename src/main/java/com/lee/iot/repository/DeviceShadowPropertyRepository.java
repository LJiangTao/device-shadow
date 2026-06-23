package com.lee.iot.repository;

import com.lee.iot.repository.entity.DeviceShadowPropertyEntity;
import com.lee.iot.repository.enums.device.ShadowPropertyStatus;
import com.lee.iot.repository.id.DeviceShadowPropertyId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;


public interface DeviceShadowPropertyRepository extends JpaRepository<DeviceShadowPropertyEntity, DeviceShadowPropertyId> {

    Optional<DeviceShadowPropertyEntity> findByDeviceIdAndPropertyKey(Long deviceId, String propertyKey);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            update DeviceShadowPropertyEntity p
               set p.desiredValue = :desiredValue,
                   p.desiredVersion = :targetVersion,
                   p.desiredOperationId = :operationId,
                   p.status = :status,
                   p.desiredUpdatedAt = :updatedAt
             where p.deviceId = :deviceId
               and p.propertyKey = :propertyKey
               and p.desiredVersion = :expectedVersion
            """)
    int updateDesiredWhenVersionMatches(
            @Param("deviceId") Long deviceId,
            @Param("propertyKey") String propertyKey,
            @Param("desiredValue") Object desiredValue,
            @Param("expectedVersion") Long expectedVersion,
            @Param("targetVersion") Long targetVersion,
            @Param("operationId") UUID operationId,
            @Param("status") ShadowPropertyStatus status,
            @Param("updatedAt") OffsetDateTime updatedAt
    );
}
