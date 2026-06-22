package com.lee.iot.service.device;

import com.lee.iot.controller.device.shadow.response.DeviceDesireResp;
import com.lee.iot.exception.DeviceCode;
import com.lee.iot.exception.DeviceException;
import com.lee.iot.pojo.context.device.desire.DeviceShadowDesireSetContext;
import com.lee.iot.repository.DeviceRepository;
import com.lee.iot.repository.entity.DeviceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceDesireOperationService {

    private final DeviceRepository deviceRepo;


    public DeviceDesireResp setDeviceDesire(DeviceShadowDesireSetContext context)
            throws DeviceException {

        if (!checkDeviceExists(context.payload().getDeviceKey()))
            throw new DeviceException(DeviceCode.DEVICE_NOT_EXISTS);



        // 1. check device exists
        // 2. check property exists
        // 3. check property version

    }

    private boolean checkDeviceExists(String deviceKey) {
        var device = DeviceEntity.builder().deviceKey(deviceKey).build();
        return deviceRepo.exists(Example.of(device));
    }
}
