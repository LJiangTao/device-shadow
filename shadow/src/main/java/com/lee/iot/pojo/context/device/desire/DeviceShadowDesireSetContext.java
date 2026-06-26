package com.lee.iot.pojo.context.device.desire;

import com.lee.iot.controller.device.shadow.req.DeviceShadowDesireReq;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public record DeviceShadowDesireSetContext(
        HttpServletRequest request,
        HttpServletResponse response,
        DeviceShadowDesireReq payload
) {

}
