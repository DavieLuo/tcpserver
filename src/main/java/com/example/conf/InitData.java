package com.example.conf;

import com.example.nettyclient.ClientUtil;
import com.example.nettyserver.ServerUtil;
import com.example.nettywebsocket.WebSocketUtil;
import com.example.unit.ToWebmsgThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Component
public class InitData  {

    @Autowired
    private ClearRedisData clearRedisData;

    @PostConstruct
    public void Init(){
        clearRedisData.clearData();//清除redis 缓存
        WebSocketUtil.getInstance().startWebService();

    }


    @PreDestroy
    public void destroyClient(){
        BaseConf.getInstance().getSendDataUtil().stopThread();
        ClientUtil.getInstance().closeClient();
        WebSocketUtil.getInstance().stopWebService();
        ServerUtil.getInstance().stopService();
    }


}
