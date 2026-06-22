package com.lee.iot.service.device;

import com.lee.iot.controller.device.shadow.response.DeviceDesireResp;
import com.lee.iot.exception.DeviceException;
import com.lee.iot.pojo.context.device.desire.DeviceShadowDesireSetContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceDesireOperationService {


    public DeviceDesireResp setDeviceDesire(DeviceShadowDesireSetContext context)
            throws DeviceException {
        // 1. check device exists
        // 2. check property exists
        // 3. check property version

    }
}
