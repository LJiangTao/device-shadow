package com.lee.iot.controller.device.shadow.response;


/**
 * 设备影子期待响应
 *
 * @param operationId    当成功设定后的操作 ID
 * @param currentVersion 当前期待版本号
 * @param currentDesire  当前期待值
 * @param message        错误消息
 * @param status         设定状态
 * @param <T>
 */
public record DeviceDesireResp<T>(
        String operationId,
        Long currentVersion,
        T currentDesire,
        String message,
        String status
) {


}
