package com.example.unit;

import com.example.conf.SpringContextUtils;
import com.example.nettywebsocket.WebSocketUtil;
import com.example.service.KeepDateService;

public class ToWebmsgThread  extends Thread{

    private KeepDateService keepDateService = (KeepDateService) SpringContextUtils.getBean(KeepDateService.class);

    @Override
    public void run() {
        msgToWeb();
    }

    public void msgToWeb(){
        try{
            while(true){
                String[] strings = keepDateService.getDataDetail();
                if(strings!=null&&strings.length==2){
                    WebSocketUtil.getInstance().sendMsg(strings[0],strings[1]);
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
