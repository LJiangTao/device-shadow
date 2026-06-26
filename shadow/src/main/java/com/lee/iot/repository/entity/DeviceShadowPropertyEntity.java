package com.lee.iot.repository.entity;


import com.lee.iot.repository.enums.device.ShadowPropertyStatus;
import com.lee.iot.repository.id.DeviceShadowPropertyId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "device_shadow_property",
        indexes = {
                @Index(name = "idx_shadow_property_device_id", columnList = "device_id"),
                @Index(name = "idx_shadow_property_key", columnList = "property_key"),
                @Index(name = "idx_shadow_property_status", columnList = "status")
        }
)
@IdClass(DeviceShadowPropertyId.class)
public class DeviceShadowPropertyEntity {


    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Id
    @Column(name = "device_id", nullable = false)
    private Long deviceId;

    @Id
    @Column(name = "property_key", nullable = false, length = 128)
    private String propertyKey;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "desired_value", columnDefinition = "jsonb")
    private Object desiredValue;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "reported_value", columnDefinition = "jsonb")
    private Object reportedValue;

    @Column(name = "desired_value_text")
    private String desiredValueText;

    @Column(name = "reported_value_text")
    private String reportedValueText;

    @Column(name = "desired_value_numeric")
    private BigDecimal desiredValueNumeric;

    @Column(name = "reported_value_numeric")
    private BigDecimal reportedValueNumeric;

    @Column(name = "desired_value_bool")
    private Boolean desiredValueBool;

    @Column(name = "reported_value_bool")
    private Boolean reportedValueBool;

    @Column(name = "desired_value_time")
    private OffsetDateTime desiredValueTime;

    @Column(name = "reported_value_time")
    private OffsetDateTime reportedValueTime;

    @Column(name = "desired_version", nullable = false)
    private Long desiredVersion;

    @Column(name = "reported_version", nullable = false)
    private Long reportedVersion;

    @Column(name = "desired_operation_id")
    private UUID desiredOperationId;

    @Column(name = "reported_operation_id")
    private UUID reportedOperationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private ShadowPropertyStatus status;

    @Column(name = "desired_updated_at")
    private OffsetDateTime desiredUpdatedAt;

    @Column(name = "reported_updated_at")
    private OffsetDateTime reportedUpdatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
