package com.example.entity;

import java.time.LocalDateTime;

public class WebSocketInfo {

    private String socketAddress;
    private String type;
    private String msg;
    private LocalDateTime actionTime;

    public WebSocketInfo() {
    }

    public WebSocketInfo(String socketAddress, String type, String msg, LocalDateTime actionTime) {
        this.socketAddress = socketAddress;
        this.type = type;
        this.msg = msg;
        this.actionTime = actionTime;
    }

    public String getSocketAddress() {
        return socketAddress;
    }

    public void setSocketAddress(String socketAddress) {
        this.socketAddress = socketAddress;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }
}
