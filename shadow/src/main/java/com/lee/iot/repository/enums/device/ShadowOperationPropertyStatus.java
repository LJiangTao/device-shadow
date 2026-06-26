package com.lee.iot.repository.enums.device;


public enum ShadowOperationPropertyStatus {

    /**
     * 等待处理
     */
    PENDING,

    /**
     * desired 已写入
     */
    DESIRED_UPDATED,

    /**
     * 已下发给设备
     */
    SENT,

    /**
     * 设备已确认生效
     */
    APPLIED,

    /**
     * 写入时版本冲突
     */
    CONFLICT,

    /**
     * 被后续操作覆盖
     */
    SUPERSEDED,

    /**
     * 设备执行失败
     */
    FAILED,

    /**
     * 超时未确认
     */
    TIMEOUT,

    /**
     * 已取消
     */
    CANCELLED
}