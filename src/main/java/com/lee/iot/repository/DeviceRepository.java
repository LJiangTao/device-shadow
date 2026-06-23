package com.lee.iot.repository;

import com.lee.iot.repository.entity.DeviceEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.Optional;

public interface DeviceRepository extends BaseJpaRepository<DeviceEntity, Long> {

    Optional<DeviceEntity> findByDeviceKey(String deviceKey);

}
