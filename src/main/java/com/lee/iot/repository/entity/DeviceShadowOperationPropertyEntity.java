package com.lee.iot.repository.entity;

import com.lee.iot.repository.enums.device.ShadowOperationPropertyStatus;
import com.lee.iot.repository.id.DeviceShadowOperationPropertyId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "device_shadow_operation_property",
        indexes = {
                @Index(name = "idx_shadow_operation_property_device", columnList = "device_id"),
                @Index(name = "idx_shadow_operation_property_key", columnList = "property_key"),
                @Index(name = "idx_shadow_operation_property_status", columnList = "status"),
                @Index(name = "idx_shadow_operation_property_target_version", columnList = "device_id, property_key, target_version"),
                @Index(name = "idx_shadow_operation_property_created_at", columnList = "created_at")
        }
)
public class DeviceShadowOperationPropertyEntity {

    @EmbeddedId
    private DeviceShadowOperationPropertyId id;

    @MapsId("operationId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operation_id", nullable = false)
    private DeviceShadowOperationEntity operation;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    /**
     * 本次操作期望写入的属性值。
     *
     * 例如：
     * true
     * 26
     * "cool"
     * {"lat":31.2,"lng":121.5}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "desired_value", nullable = false, columnDefinition = "jsonb")
    private Object desiredValue;

    /**
     * 用户提交时基于的属性版本。
     * 用于乐观锁。
     */
    @Column(name = "expected_version")
    private Long expectedVersion;

    /**
     * 平台成功写入 desired 后生成的新版本。
     */
    @Column(name = "target_version")
    private Long targetVersion;

    /**
     * 设备最终上报的值。
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reported_value", columnDefinition = "jsonb")
    private Object reportedValue;

    /**
     * 设备上报时确认的版本。
     */
    @Column(name = "reported_version")
    private Long reportedVersion;

    /**
     * 当前属性操作状态。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShadowOperationPropertyStatus status;

    /**
     * 设备确认生效时间。
     */
    @Column(name = "applied_at")
    private OffsetDateTime appliedAt;

    /**
     * 执行失败时间。
     */
    @Column(name = "failed_at")
    private OffsetDateTime failedAt;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public String getPropertyKey() {
        return id != null ? id.getPropertyKey() : null;
    }
}