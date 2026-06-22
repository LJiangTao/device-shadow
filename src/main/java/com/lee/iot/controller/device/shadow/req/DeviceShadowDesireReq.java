package com.lee.iot.controller.device.shadow.req;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@Data
public class DeviceShadowDesireReq implements Serializable {

    /**
     * 设备编码
     */
    private String deviceKey;

    /**
     * 期待属性
     */
    private List<DesireValue> values;


    @Data
    @Builder
    @ToString
    static class DesireValue {

        /**
         * 期望版本
         */
        private Long desireVersion;

        /**
         * 当前属性版本
         */
        private Long expectedVersion;

        /**
         * 属性名称
         */
        private String property;

        /**
         * 期待值
         */
        private Object desireValue;

    }

}
