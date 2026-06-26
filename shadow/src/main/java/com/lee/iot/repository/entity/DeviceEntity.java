package com.lee.iot.repository.entity;

import com.lee.iot.repository.enums.device.DeviceOnlineStatus;
import com.lee.iot.repository.enums.device.DeviceProtocol;
import com.lee.iot.repository.enums.device.DeviceStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "iot_device",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_iot_device_product_device_key",
                        columnNames = {"product_id", "device_key"}
                )
        },
        indexes = {
                @Index(name = "idx_iot_device_tenant_id", columnList = "tenant_id"),
                @Index(name = "idx_iot_device_product_id", columnList = "product_id"),
                @Index(name = "idx_iot_device_status", columnList = "status"),
                @Index(name = "idx_iot_device_online_status", columnList = "online_status"),
                @Index(name = "idx_iot_device_created_at", columnList = "created_at")
        }
)
public class DeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 租户 ID，多租户系统建议保留
     */
    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    /**
     * 产品 ID，用于表示设备属于哪个产品模型
     */
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /**
     * 设备唯一标识。
     * 通常是设备 SN、IMEI、MAC、自定义设备编号等。
     */
    @Column(name = "device_key", nullable = false, length = 128)
    private String deviceKey;

    /**
     * 设备名称，给用户看的名称
     */
    @Column(name = "device_name", length = 128)
    private String deviceName;

    /**
     * 设备密钥，用于设备认证。
     * 生产环境建议加密存储或只保存 hash。
     */
    @Column(name = "device_secret", length = 256)
    private String deviceSecret;

    /**
     * 设备状态：
     * ACTIVE / DISABLED / INACTIVE / DELETED
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private DeviceStatus status;

    /**
     * 在线状态：
     * ONLINE / OFFLINE / UNKNOWN
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "online_status", nullable = false, length = 32)
    private DeviceOnlineStatus onlineStatus;

    /**
     * 协议类型：
     * MQTT / HTTP / COAP / TCP / UDP
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "protocol", length = 32)
    private DeviceProtocol protocol;

    /**
     * MQTT clientId 或其他连接标识
     */
    @Column(name = "client_id", length = 128)
    private String clientId;

    /**
     * 设备固件版本
     */
    @Column(name = "firmware_version", length = 64)
    private String firmwareVersion;

    /**
     * 设备软件版本
     */
    @Column(name = "software_version", length = 64)
    private String softwareVersion;

    /**
     * 硬件版本
     */
    @Column(name = "hardware_version", length = 64)
    private String hardwareVersion;

    /**
     * 设备型号
     */
    @Column(name = "model", length = 128)
    private String model;

    /**
     * 厂商
     */
    @Column(name = "manufacturer", length = 128)
    private String manufacturer;

    /**
     * 最近一次连接 IP
     */
    @Column(name = "last_ip", length = 64)
    private String lastIp;

    /**
     * 激活时间
     */
    @Column(name = "activated_at")
    private OffsetDateTime activatedAt;

    /**
     * 最近上线时间
     */
    @Column(name = "last_online_at")
    private OffsetDateTime lastOnlineAt;

    /**
     * 最近离线时间
     */
    @Column(name = "last_offline_at")
    private OffsetDateTime lastOfflineAt;

    /**
     * 最近上报时间
     */
    @Column(name = "last_reported_at")
    private OffsetDateTime lastReportedAt;

    /**
     * 标签，例如：
     * {
     *   "region": "shanghai",
     *   "group": "factory-a"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tags", columnDefinition = "jsonb")
    private Map<String, Object> tags;

    /**
     * 扩展元数据，例如：
     * {
     *   "installLocation": "building-1",
     *   "installer": "zhangsan"
     * }
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    /**
     * 备注
     */
    @Column(name = "remark", length = 512)
    private String remark;

    /**
     * 创建人
     */
    @Column(name = "created_by")
    private Long createdBy;

    /**
     * 更新人
     */
    @Column(name = "updated_by")
    private Long updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    /**
     * 软删除标识
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;
}
