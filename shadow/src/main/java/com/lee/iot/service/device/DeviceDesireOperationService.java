package com.lee.iot.service.device;

import com.lee.iot.controller.device.shadow.req.DeviceShadowDesireReq;
import com.lee.iot.controller.device.shadow.response.DeviceDesireResp;
import com.lee.iot.exception.DeviceCode;
import com.lee.iot.exception.DeviceException;
import com.lee.iot.pojo.context.device.desire.DeviceShadowDesireSetContext;
import com.lee.iot.repository.DeviceRepository;
import com.lee.iot.repository.DeviceShadowOperationPropertyRepository;
import com.lee.iot.repository.DeviceShadowOperationRepository;
import com.lee.iot.repository.DeviceShadowPropertyRepository;
import com.lee.iot.repository.entity.DeviceEntity;
import com.lee.iot.repository.entity.DeviceShadowOperationEntity;
import com.lee.iot.repository.entity.DeviceShadowOperationPropertyEntity;
import com.lee.iot.repository.entity.DeviceShadowPropertyEntity;
import com.lee.iot.repository.enums.device.ShadowOperationPropertyStatus;
import com.lee.iot.repository.enums.device.ShadowOperationStatus;
import com.lee.iot.repository.enums.device.ShadowOperationType;
import com.lee.iot.repository.enums.device.ShadowPropertyStatus;
import com.lee.iot.repository.id.DeviceShadowOperationPropertyId;
import com.lee.iot.util.UUID;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class DeviceDesireOperationService {

    private final DeviceRepository deviceRepo;
    private final DeviceShadowPropertyRepository shadowPropertyRepo;
    private final DeviceShadowOperationRepository operationRepo;
    private final DeviceShadowOperationPropertyRepository operationPropertyRepo;
    private final PlatformTransactionManager transactionManager;

    public DeviceDesireResp<Object> setDeviceDesire(DeviceShadowDesireSetContext context)
            throws DeviceException {
        DeviceShadowDesireReq payload = validatePayload(context.payload());
        DeviceEntity device = deviceRepo.findByDeviceKey(payload.getDeviceKey())
                .orElseThrow(() -> new DeviceException(DeviceCode.DEVICE_NOT_EXISTS));

        var operationId = UUID.randomTimeBaseUUID();
        List<DeviceShadowDesireReq.DesireValue> values = payload.getValues();

        try {
            return requiredTransaction().execute(status -> setDeviceDesireInTransaction(device, operationId, payload, values));
        } catch (ShadowVersionConflictException e) {
            writeConflictOperation(device, operationId, payload, values, e.getPropertyKey(), e.getMessage());
            throw new DeviceException(DeviceCode.DEVICE_SHADOW_VERSION_CONFLICT);
        } catch (DataIntegrityViolationException e) {
            writeConflictOperation(device, operationId, payload, values, null, e.getMessage());
            throw new DeviceException(DeviceCode.DEVICE_SHADOW_VERSION_CONFLICT);
        }
    }

    private DeviceDesireResp<Object> setDeviceDesireInTransaction(DeviceEntity device,
                                                                 java.util.UUID operationId,
                                                                 DeviceShadowDesireReq payload,
                                                                 List<DeviceShadowDesireReq.DesireValue> values) {
        DeviceShadowOperationEntity operation = createOperation(device, operationId, payload, ShadowOperationStatus.PENDING, null);
        operationRepo.saveAndFlush(operation);

        Long currentVersion = null;
        Object currentDesire = null;
        OffsetDateTime now = OffsetDateTime.now();

        for (DeviceShadowDesireReq.DesireValue value : values) {
            Long targetVersion = writeDesiredProperty(device, operationId, value, now);
            operationPropertyRepo.save(createOperationProperty(
                    operation,
                    device,
                    value,
                    targetVersion,
                    ShadowOperationPropertyStatus.DESIRED_UPDATED,
                    null
            ));
            currentVersion = targetVersion;
            currentDesire = value.getDesireValue();
        }

        operationPropertyRepo.flush();
        return new DeviceDesireResp<>(operationId.toString(), currentVersion, currentDesire, null, ShadowOperationStatus.PENDING.name());
    }

    private Long writeDesiredProperty(DeviceEntity device,
                                      java.util.UUID operationId,
                                      DeviceShadowDesireReq.DesireValue value,
                                      OffsetDateTime now) {
        var current = shadowPropertyRepo.findByDeviceIdAndPropertyKey(device.getId(), value.getProperty());
        if (current.isPresent()) {
            Long expectedVersion = value.getExpectedVersion();
            if (expectedVersion == null) {
                throw new ShadowVersionConflictException(value.getProperty(), "expectedVersion is required");
            }
            Long targetVersion = expectedVersion + 1;
            int updated = shadowPropertyRepo.updateDesiredWhenVersionMatches(
                    device.getId(),
                    value.getProperty(),
                    value.getDesireValue(),
                    expectedVersion,
                    targetVersion,
                    operationId,
                    ShadowPropertyStatus.PENDING,
                    now
            );
            if (updated != 1) {
                throw new ShadowVersionConflictException(value.getProperty(), "desired version conflict");
            }
            return targetVersion;
        }

        if (value.getExpectedVersion() != null && value.getExpectedVersion() != 0) {
            throw new ShadowVersionConflictException(value.getProperty(), "new property expectedVersion must be null or 0");
        }

        DeviceShadowPropertyEntity property = DeviceShadowPropertyEntity.builder()
                .tenantId(device.getTenantId())
                .deviceId(device.getId())
                .propertyKey(value.getProperty())
                .desiredValue(value.getDesireValue())
                .desiredVersion(1L)
                .reportedVersion(0L)
                .desiredOperationId(operationId)
                .status(ShadowPropertyStatus.PENDING)
                .desiredUpdatedAt(now)
                .build();
        shadowPropertyRepo.saveAndFlush(property);
        return 1L;
    }

    private void writeConflictOperation(DeviceEntity device,
                                        java.util.UUID operationId,
                                        DeviceShadowDesireReq payload,
                                        List<DeviceShadowDesireReq.DesireValue> values,
                                        String conflictProperty,
                                        String message) {
        requiresNewTransaction().executeWithoutResult(status -> {
            DeviceShadowOperationEntity operation = createOperation(
                    device,
                    operationId,
                    payload,
                    ShadowOperationStatus.CONFLICT,
                    message
            );
            operation.setFinishedAt(OffsetDateTime.now());
            operationRepo.saveAndFlush(operation);

            for (DeviceShadowDesireReq.DesireValue value : values) {
                ShadowOperationPropertyStatus propertyStatus = Objects.equals(value.getProperty(), conflictProperty)
                        || conflictProperty == null
                        ? ShadowOperationPropertyStatus.CONFLICT
                        : ShadowOperationPropertyStatus.CANCELLED;
                DeviceShadowOperationPropertyEntity property = createOperationProperty(
                        operation,
                        device,
                        value,
                        null,
                        propertyStatus,
                        propertyStatus == ShadowOperationPropertyStatus.CONFLICT ? message : "cancelled by version conflict"
                );
                if (propertyStatus == ShadowOperationPropertyStatus.CONFLICT) {
                    property.setFailedAt(OffsetDateTime.now());
                }
                operationPropertyRepo.save(property);
            }
            operationPropertyRepo.flush();
        });
    }

    private DeviceShadowOperationEntity createOperation(DeviceEntity device,
                                                        java.util.UUID operationId,
                                                       DeviceShadowDesireReq payload,
                                                       ShadowOperationStatus status,
                                                       String errorMessage) {
        return DeviceShadowOperationEntity.builder()
                .operationId(operationId)
                .tenantId(device.getTenantId())
                .deviceId(device.getId())
                .operationType(ShadowOperationType.UPDATE_DESIRED)
                .status(status)
                .requestBody(toRequestBody(payload))
                .errorCode(errorMessage == null ? null : DeviceCode.DEVICE_SHADOW_VERSION_CONFLICT.getCode())
                .errorMessage(errorMessage)
                .build();
    }

    private DeviceShadowOperationPropertyEntity createOperationProperty(DeviceShadowOperationEntity operation,
                                                                        DeviceEntity device,
                                                                        DeviceShadowDesireReq.DesireValue value,
                                                                        Long targetVersion,
                                                                        ShadowOperationPropertyStatus status,
                                                                        String errorMessage) {
        return DeviceShadowOperationPropertyEntity.builder()
                .id(new DeviceShadowOperationPropertyId(operation.getOperationId(), value.getProperty()))
                .operation(operation)
                .tenantId(device.getTenantId())
                .deviceId(device.getId())
                .desiredValue(value.getDesireValue())
                .expectedVersion(value.getExpectedVersion())
                .targetVersion(targetVersion)
                .status(status)
                .errorCode(errorMessage == null ? null : DeviceCode.DEVICE_SHADOW_VERSION_CONFLICT.getCode())
                .errorMessage(errorMessage)
                .build();
    }

    private DeviceShadowDesireReq validatePayload(DeviceShadowDesireReq payload) {
        Set<String> properties = new HashSet<>();
        for (DeviceShadowDesireReq.DesireValue value : payload.getValues()) {
            if (value == null || StringUtils.isBlank(value.getProperty()) || !properties.add(value.getProperty())) {
                throw new DeviceException(DeviceCode.DEVICE_SHADOW_INVALID_REQUEST);
            }
        }
        return payload;
    }

    private Map<String, Object> toRequestBody(DeviceShadowDesireReq payload) {
        Map<String, Object> body = new HashMap<>();
        body.put("deviceKey", payload.getDeviceKey());
        body.put("values", payload.getValues());
        return body;
    }

    private TransactionTemplate requiredTransaction() {
        var definition = new DefaultTransactionDefinition();
        definition.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        TransactionTemplate template = new TransactionTemplate(transactionManager, definition);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        return template;
    }

    private TransactionTemplate requiresNewTransaction() {
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        return template;
    }

    private static class ShadowVersionConflictException extends RuntimeException {

        private final String propertyKey;

        private ShadowVersionConflictException(String propertyKey, String message) {
            super(message);
            this.propertyKey = propertyKey;
        }

        private String getPropertyKey() {
            return propertyKey;
        }
    }
}
