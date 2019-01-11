package com.example.entity;

import java.time.LocalDateTime;

public class ClientOnAndOffline {

    private String clientSocket;
    private String type; //上线 下线
    private LocalDateTime actionTime;

    public ClientOnAndOffline() {
    }

    public ClientOnAndOffline(String clientSocket, String type, LocalDateTime actionTime) {
        this.clientSocket = clientSocket;
        this.type = type;
        this.actionTime = actionTime;
    }

    public String getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(String clientSocket) {
        this.clientSocket = clientSocket;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getActionTime() {
        return actionTime;
    }

    public void setActionTime(LocalDateTime actionTime) {
        this.actionTime = actionTime;
    }
}
