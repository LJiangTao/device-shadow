package com.lee.iot.repository.enums.device;

public enum ShadowOperationStatus {

    /**
     * 平台已接受请求
     */
    ACCEPTED,

    /**
     * desired 已写入，等待设备确认
     */
    PENDING,

    /**
     * 已下发给设备
     */
    SENT,

    /**
     * 设备已确认生效
     */
    APPLIED,

    /**
     * 部分属性已成功，部分失败
     */
    PARTIAL_APPLIED,

    /**
     * 写入时发生版本冲突
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
