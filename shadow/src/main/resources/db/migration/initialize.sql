CREATE TABLE device_shadow_operation (
                                         operation_id UUID PRIMARY KEY,

                                         tenant_id BIGINT NOT NULL,
                                         device_id BIGINT NOT NULL,
                                         user_id BIGINT,

                                         request_id VARCHAR(128),

                                         operation_type VARCHAR(32) NOT NULL,

                                         status VARCHAR(32) NOT NULL,

                                         request_body JSONB NOT NULL,

                                         timeout_at TIMESTAMPTZ,
                                         finished_at TIMESTAMPTZ,

                                         error_code VARCHAR(64),
                                         error_message TEXT,

                                         created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                         updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE UNIQUE INDEX uk_shadow_operation_request
    ON device_shadow_operation(device_id, request_id)
    WHERE request_id IS NOT NULL;

CREATE INDEX idx_shadow_operation_tenant_id
    ON device_shadow_operation(tenant_id);

CREATE INDEX idx_shadow_operation_device_id
    ON device_shadow_operation(device_id);

CREATE INDEX idx_shadow_operation_user_id
    ON device_shadow_operation(user_id);

CREATE INDEX idx_shadow_operation_status
    ON device_shadow_operation(status);

CREATE INDEX idx_shadow_operation_created_at
    ON device_shadow_operation(created_at);


CREATE TABLE iot_device (
                            id BIGSERIAL PRIMARY KEY,

                            tenant_id BIGINT NOT NULL,
                            product_id BIGINT NOT NULL,

                            device_key VARCHAR(128) NOT NULL,
                            device_name VARCHAR(128),
                            device_secret VARCHAR(256),

                            status VARCHAR(32) NOT NULL,
                            online_status VARCHAR(32) NOT NULL,
                            protocol VARCHAR(32),

                            client_id VARCHAR(128),

                            firmware_version VARCHAR(64),
                            software_version VARCHAR(64),
                            hardware_version VARCHAR(64),

                            model VARCHAR(128),
                            manufacturer VARCHAR(128),

                            last_ip VARCHAR(64),

                            activated_at TIMESTAMPTZ,
                            last_online_at TIMESTAMPTZ,
                            last_offline_at TIMESTAMPTZ,
                            last_reported_at TIMESTAMPTZ,

                            tags JSONB,
                            metadata JSONB,

                            remark VARCHAR(512),

                            created_by BIGINT,
                            updated_by BIGINT,

                            created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                            updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                            deleted BOOLEAN NOT NULL DEFAULT false,

                            CONSTRAINT uk_iot_device_product_device_key
                                UNIQUE (product_id, device_key)
);

CREATE INDEX idx_iot_device_tenant_id
    ON iot_device (tenant_id);

CREATE INDEX idx_iot_device_product_id
    ON iot_device (product_id);

CREATE INDEX idx_iot_device_status
    ON iot_device (status);

CREATE INDEX idx_iot_device_online_status
    ON iot_device (online_status);

CREATE INDEX idx_iot_device_created_at
    ON iot_device (created_at);


CREATE TABLE device_shadow_operation_property (
                                                  operation_id UUID NOT NULL,
                                                  property_key VARCHAR(128) NOT NULL,

                                                  tenant_id BIGINT NOT NULL,
                                                  device_id BIGINT NOT NULL,

                                                  desired_value JSONB NOT NULL,

                                                  expected_version BIGINT,
                                                  target_version BIGINT,

                                                  reported_value JSONB,
                                                  reported_version BIGINT,

                                                  status VARCHAR(32) NOT NULL,

                                                  applied_at TIMESTAMPTZ,
                                                  failed_at TIMESTAMPTZ,

                                                  error_code VARCHAR(64),
                                                  error_message TEXT,

                                                  created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                                  updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                                  PRIMARY KEY (operation_id, property_key),

                                                  CONSTRAINT fk_shadow_operation_property_operation
                                                      FOREIGN KEY (operation_id)
                                                          REFERENCES device_shadow_operation(operation_id)
                                                          ON DELETE CASCADE
);

CREATE INDEX idx_shadow_operation_property_device
    ON device_shadow_operation_property(device_id);

CREATE INDEX idx_shadow_operation_property_key
    ON device_shadow_operation_property(property_key);

CREATE INDEX idx_shadow_operation_property_status
    ON device_shadow_operation_property(status);

CREATE INDEX idx_shadow_operation_property_target_version
    ON device_shadow_operation_property(device_id, property_key, target_version);

CREATE INDEX idx_shadow_operation_property_created_at
    ON device_shadow_operation_property(created_at);

CREATE TABLE device_shadow_property (
                                        tenant_id BIGINT NOT NULL,
                                        device_id BIGINT NOT NULL,
                                        property_key VARCHAR(128) NOT NULL,

    -- 平台期待值
                                        desired_value JSONB,

    -- 设备实际上报值
                                        reported_value JSONB,

    -- desired 类型化冗余字段，用于查询、排序、索引
                                        desired_value_text TEXT,
                                        desired_value_numeric NUMERIC,
                                        desired_value_bool BOOLEAN,
                                        desired_value_time TIMESTAMPTZ,

    -- reported 类型化冗余字段，用于查询、排序、索引
                                        reported_value_text TEXT,
                                        reported_value_numeric NUMERIC,
                                        reported_value_bool BOOLEAN,
                                        reported_value_time TIMESTAMPTZ,

    -- desired 版本号，用户修改期待值时递增
                                        desired_version BIGINT NOT NULL DEFAULT 0,

    -- reported 版本号，设备上报时递增或使用设备确认版本
                                        reported_version BIGINT NOT NULL DEFAULT 0,

    -- 最后一次修改 desired 的操作 ID
                                        desired_operation_id UUID,

    -- 最后一次确认 reported 的操作 ID
                                        reported_operation_id UUID,

    -- 属性状态
                                        status VARCHAR(32) NOT NULL DEFAULT 'INIT',

    -- desired 最近更新时间
                                        desired_updated_at TIMESTAMPTZ,

    -- reported 最近更新时间
                                        reported_updated_at TIMESTAMPTZ,

    -- 最近一次期望值过期时间，可选
                                        desired_expired_at TIMESTAMPTZ,

    -- 错误信息，可选
                                        error_code VARCHAR(64),
                                        error_message TEXT,

                                        created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                        updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),

                                        PRIMARY KEY (device_id, property_key)
);

-- 多租户查询
CREATE INDEX idx_shadow_property_tenant_id
    ON device_shadow_property (tenant_id);

-- 查询某个设备的全部影子属性
CREATE INDEX idx_shadow_property_device_id
    ON device_shadow_property (device_id);

-- 查询某类属性，例如 temperature / switch / mode
CREATE INDEX idx_shadow_property_key
    ON device_shadow_property (property_key);

-- 根据状态查询，例如 PENDING / TIMEOUT
CREATE INDEX idx_shadow_property_status
    ON device_shadow_property (status);

-- 查询某设备某属性的 desired 版本
CREATE INDEX idx_shadow_property_desired_version
    ON device_shadow_property (device_id, property_key, desired_version);

-- 查询某设备某属性的 reported 版本
CREATE INDEX idx_shadow_property_reported_version
    ON device_shadow_property (device_id, property_key, reported_version);

-- 根据 desired operation 查询
CREATE INDEX idx_shadow_property_desired_operation
    ON device_shadow_property (desired_operation_id)
    WHERE desired_operation_id IS NOT NULL;

-- 根据 reported operation 查询
CREATE INDEX idx_shadow_property_reported_operation
    ON device_shadow_property (reported_operation_id)
    WHERE reported_operation_id IS NOT NULL;