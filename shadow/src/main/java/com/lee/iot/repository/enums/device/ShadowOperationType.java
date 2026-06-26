package com.lee.iot.repository.enums.device;

public enum ShadowOperationType {

    /**
     * 修改 desired 期待值
     */
    UPDATE_DESIRED,

    /**
     * 清除 desired 期待值
     */
    CLEAR_DESIRED,

    /**
     * 平台根据设备上报同步 reported
     */
    SYNC_REPORTED

}
