package com.example.entity;

import java.net.SocketAddress;

public class NettyMessage {

    private String address;//socket创建之后的地址
    private String status;//在线状态 1 在线 0 /null 离线
    private String isOpenWeb; //开启与websocket 的交互

    public NettyMessage() {
    }

    public NettyMessage(String address, String status, String isOpenWeb) {
        this.address = address;
        this.status = status;
        this.isOpenWeb = isOpenWeb;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsOpenWeb() {
        return isOpenWeb;
    }

    public void setIsOpenWeb(String isOpenWeb) {
        this.isOpenWeb = isOpenWeb;
    }
}
