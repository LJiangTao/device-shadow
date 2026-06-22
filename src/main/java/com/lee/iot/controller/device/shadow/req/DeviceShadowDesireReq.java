package com.lee.iot.controller.device.shadow.req;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class DeviceShadowDesireReq implements Serializable {

    /**
     * 设备编码
     */
    private String deviceCode;

    /**
     * 期待版本
     */
    private Long desireVersion;

    /**
     * 写入参数
     */
    private Map<String, Object> params;

}
