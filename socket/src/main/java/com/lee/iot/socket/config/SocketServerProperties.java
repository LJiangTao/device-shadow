package com.lee.iot.socket.config;

public class SocketServerProperties {

    private String host = "0.0.0.0";

    private int port = 8081;

    private int socketIoPort = 8082;

    private String websocketPath = "/ws";

    private String socketIoPath = "/socket.io";

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getSocketIoPort() {
        return socketIoPort;
    }

    public void setSocketIoPort(int socketIoPort) {
        this.socketIoPort = socketIoPort;
    }

    public String getWebsocketPath() {
        return websocketPath;
    }

    public void setWebsocketPath(String websocketPath) {
        this.websocketPath = websocketPath;
    }

    public String getSocketIoPath() {
        return socketIoPath;
    }

    public void setSocketIoPath(String socketIoPath) {
        this.socketIoPath = socketIoPath;
    }
}
