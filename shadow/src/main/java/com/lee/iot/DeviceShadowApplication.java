package com.lee.iot;

import com.lee.iot.socket.config.SocketMqttProperties;
import com.lee.iot.socket.config.SocketServerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({SocketServerProperties.class, SocketMqttProperties.class})
public class DeviceShadowApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeviceShadowApplication.class, args);
    }

}
