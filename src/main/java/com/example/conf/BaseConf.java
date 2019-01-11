package com.example.conf;

import com.example.nettyclient.SendDataUtil;

public class BaseConf {

    private SendDataUtil sendDataUtil;
    private String sendData;
    private String host;
    private Integer port;
    private Integer timejg;

    private BaseConf(){}

    private static final  class SingleHolder{
        private static final BaseConf INSTANCE = new BaseConf();
    }
    public static BaseConf getInstance(){
        return  SingleHolder.INSTANCE;
    }

    public SendDataUtil getSendDataUtil() {
        return sendDataUtil;
    }

    public void setSendDataUtil(SendDataUtil sendDataUtil) {
        this.sendDataUtil = sendDataUtil;
    }

    public String getSendData() {
        return sendData;
    }

    public void setSendData(String sendData) {
        this.sendData = sendData;
        sendDataUtil.setMsg(sendData);

    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getTimejg() {
        return timejg;
    }

    public void setTimejg(Integer timejg) {
        this.timejg = timejg;
        sendDataUtil.setTimejg(timejg);

    }
}
