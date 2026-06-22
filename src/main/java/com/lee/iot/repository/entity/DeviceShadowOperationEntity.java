package com.lee.iot.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.Id;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "device_shadow_operation",
        indexes = {
                @Index(name = "idx_shadow_operation_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_shadow_operation_device_id", columnList = "device_id"),
                @Index(name = "idx_shadow_operation_user_id", columnList = "user_id"),
                @Index(name = "idx_shadow_operation_status", columnList = "status"),
                @Index(name = "idx_shadow_operation_created_at", columnList = "created_at")
        }
)
public class DeviceShadowOperationEntity {

    @Id
    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Column(name = "user_id")
    private Long userId;

    /**
     * 客户端请求 ID，用于幂等。
     */
    @Column(name = "request_id", length = 128)
    private String requestId;

    /**
     * 操作类型：
     * UPDATE_DESIRED / CLEAR_DESIRED / SYNC_REPORTED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 32)
    private ShadowOperationType operationType;

    /**
     * 整体操作状态。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShadowOperationStatus status;

    /**
     * 原始请求体。
     *
     * 示例：
     * {
     *   "desired": {
     *     "switch": true,
     *     "mode": "cool"
     *   },
     *   "expectedVersions": {
     *     "switch": 7,
     *     "mode": 2
     *   }
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "request_body", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> requestBody;

    /**
     * 操作超时时间。
     */
    @Column(name = "timeout_at")
    private OffsetDateTime timeoutAt;

    /**
     * 操作完成时间。
     */
    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

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
}