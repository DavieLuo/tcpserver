package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.ClientOnAndOffline;
import com.example.unit.Nstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OnAndOfflineService {

    @Autowired
    private RedisTemplate redisTemplate;

    public void saveClientInfo(String socketAddress,ClientOnAndOffline client){
        redisTemplate.opsForList().leftPush(Nstatus.On_Off_Line.V+socketAddress, JSONObject.toJSONString(client));
    }

    public Object getClientInfo(String socketAddress){
        return redisTemplate.opsForList().leftPop(Nstatus.On_Off_Line.V+socketAddress);
    }

    public List<ClientOnAndOffline> queryAllClientInfo(String socketAddress){
       List<Object> ls= redisTemplate.opsForList().range(Nstatus.On_Off_Line.V+socketAddress,0,-1);
       List<ClientOnAndOffline> clients = new ArrayList<>();
       ls.forEach(client->{
           ClientOnAndOffline clientinfo = JSONObject.parseObject(client.toString(),ClientOnAndOffline.class);
           clients.add(clientinfo);
       });
        return clients;
    }
}
