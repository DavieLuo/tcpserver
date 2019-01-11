package com.example.conf;

import com.example.unit.Nstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ClearRedisData {

    @Autowired
    private RedisTemplate redisTemplate;

    public void clearData(){
        System.out.println("执行 redis 数据清空");
        redisTemplate.delete(redisTemplate.keys(Nstatus.NettyList.V+"*"));//删除nettuMessage shuju
        redisTemplate.delete(redisTemplate.keys(Nstatus.SendInfo.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.RecInfo.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.NettyServerList.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.On_Off_Line.V+"*"));
        redisTemplate.delete(Nstatus.Msg_to_web.V);
    }

    public void clearDataForclient(){

        redisTemplate.delete(redisTemplate.keys(Nstatus.NettyList.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.SendInfo.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.RecInfo.V+"*"));
        redisTemplate.delete(Nstatus.Msg_to_web.V);
    }
    public void clearDataForServer(){
        System.out.println("执行 redis 数据清空");

        redisTemplate.delete(redisTemplate.keys(Nstatus.SendInfo.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.RecInfo.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.NettyServerList.V+"*"));
        redisTemplate.delete(redisTemplate.keys(Nstatus.On_Off_Line.V+"*"));
        redisTemplate.delete(Nstatus.Msg_to_web.V);
    }
}
