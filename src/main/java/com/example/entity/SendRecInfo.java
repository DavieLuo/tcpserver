package com.example.entity;

public class SendRecInfo {

    private Long recNum;
    private Long recData;
    private Long sendNum;
    private Long sendData;


    public SendRecInfo() {
    }

    public SendRecInfo(Long recNum, Long recData, Long sendNum, Long sendData) {
        this.recNum = recNum;
        this.recData = recData;
        this.sendNum = sendNum;
        this.sendData = sendData;
    }

    public Long getRecNum() {
        return recNum;
    }

    public void setRecNum(Long recNum) {
        this.recNum = recNum;
    }

    public Long getRecData() {
        return recData;
    }

    public void setRecData(Long recData) {
        this.recData = recData;
    }

    public Long getSendNum() {
        return sendNum;
    }

    public void setSendNum(Long sendNum) {
        this.sendNum = sendNum;
    }

    public Long getSendData() {
        return sendData;
    }

    public void setSendData(Long sendData) {
        this.sendData = sendData;
    }
}
