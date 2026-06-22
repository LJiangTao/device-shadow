package com.lee.iot.repository;

import com.lee.iot.repository.entity.DeviceShadowPropertyEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;


public interface DeviceShadowPropertyRepository extends
        BaseJpaRepository<DeviceShadowPropertyEntity, Long>,
        ListPagingAndSortingRepository<DeviceShadowPropertyEntity, Long> {


}
