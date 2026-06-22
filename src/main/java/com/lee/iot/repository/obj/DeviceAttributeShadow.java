package com.lee.iot.repository.obj;


import io.hypersistence.utils.hibernate.type.json.internal.JsonBinaryJdbcTypeDescriptor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.Type;

@Entity
public class DeviceAttributeShadow {
    @Id
    @GeneratedValue
    private Long id;

    private String deviceCode;

    private String attributeName;

    private Long version;

    @JdbcType(JsonBinaryJdbcTypeDescriptor.class)
    private String value;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


}
