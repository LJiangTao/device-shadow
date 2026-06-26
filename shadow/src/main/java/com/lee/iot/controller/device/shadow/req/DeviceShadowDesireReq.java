package com.lee.iot.controller.device.shadow.req;

import com.lee.iot.exception.DeviceCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    private String deviceKey;

    /**
     * 期待属性
     */
    @Valid
    @NotNull
    private List<DesireValue> values;


    @Data
    @Builder
    @ToString
    public static class DesireValue {

        /**
         * 当前属性版本
         */
        @NotNull
        private Long expectedVersion;

        /**
         * 属性名称
         */
        @NotNull
        private String property;

        /**
         * 期待值
         */
        @NotNull
        private Object desireValue;

    }

}
