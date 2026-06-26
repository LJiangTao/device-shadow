package com.lee.iot.controller.device.shadow;


import com.lee.iot.controller.device.shadow.req.DeviceShadowDesireReq;
import com.lee.iot.controller.device.shadow.response.DeviceDesireResp;
import com.lee.iot.pojo.context.device.desire.DeviceShadowDesireSetContext;
import com.lee.iot.service.device.DeviceDesireOperationService;
import com.lee.iot.util.web.R;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 设备影子操作
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/device/desire")
public class DeviceDesireController {

    private final DeviceDesireOperationService desiresOperationService;

    @PostMapping
    public R<DeviceDesireResp> setDesire(HttpServletRequest request,
                                         HttpServletResponse response,
                                         @RequestBody DeviceShadowDesireReq payload) {
        var context = new DeviceShadowDesireSetContext(request, response, payload);
        return R.ok(desiresOperationService.setDeviceDesire(context));
    }



}
