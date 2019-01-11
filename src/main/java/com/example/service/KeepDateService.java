package com.example.service;

import com.example.entity.SendRecInfo;
import com.example.unit.Nstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class KeepDateService {

    @Autowired
    private RedisTemplate redisTemplate;


    public void recNumRise(String socketAddress,Long recData){
        Map<String,String> map =new HashMap<>();
        map.put(Nstatus.RecNum.V,getRecNumInfo(socketAddress)+1+"");
        map.put(Nstatus.RecData.V,getRecDataInfo(socketAddress)+recData+"");
        redisTemplate.opsForHash().putAll(Nstatus.RecInfo.V+socketAddress,map);
    }

    public Long getRecNumInfo(String address){
        Object object = redisTemplate.opsForHash().get(Nstatus.RecInfo.V+address,Nstatus.RecNum.V);
        return  object==null?0: Long.parseLong(object.toString());
    }

    public Long getRecDataInfo(String address){
        Object object = redisTemplate.opsForHash().get(Nstatus.RecInfo.V+address,Nstatus.RecData.V);
        return object==null?0: Long.parseLong(object.toString());
    }


    public void sendNumRise(String socketAddress,Long sendData){
        Map<String,String> map =new HashMap<>();
        map.put(Nstatus.SendNum.V,getSendNumInfo(socketAddress)+1+"");
        map.put(Nstatus.SendData.V,getSendDataInfo(socketAddress)+sendData+"");
        redisTemplate.opsForHash().putAll(Nstatus.SendInfo.V+socketAddress,map);
    }

    public Long getSendNumInfo(String address){
        Object object = redisTemplate.opsForHash().get(Nstatus.SendInfo.V+address,Nstatus.SendNum.V);
        return object==null?0:Long.parseLong(object.toString()) ;
    }

    public Long getSendDataInfo(String address){
        Object object = redisTemplate.opsForHash().get(Nstatus.SendInfo.V+address,Nstatus.SendData.V);
        return object==null?0:Long.parseLong(object.toString());
    }



    public SendRecInfo getInfo(String socketAddress){
        return  new SendRecInfo(getRecNumInfo(socketAddress),getRecDataInfo(socketAddress),getSendNumInfo(socketAddress),getSendDataInfo(socketAddress));
    }

    public Long getSendNumInfobase(String k){
        Object object = redisTemplate.opsForHash().get(k,Nstatus.SendNum.V);
        return object==null?0:Long.parseLong(object.toString()) ;
    }

    public Long getSendDataInfobase(String k){
        Object object = redisTemplate.opsForHash().get(k,Nstatus.SendData.V);
        return object==null?0:Long.parseLong(object.toString());
    }

    public Long getRecNumInfobase(String k){
        Object object = redisTemplate.opsForHash().get(k,Nstatus.RecNum.V);
        return  object==null?0: Long.parseLong(object.toString());
    }

    public Long getRecDataInfobase(String k){
        Object object = redisTemplate.opsForHash().get(k,Nstatus.RecData.V);
        return object==null?0: Long.parseLong(object.toString());
    }


    public SendRecInfo getAllSendAndRecInfo(){
        SendRecInfo sendRecInfo = new SendRecInfo(0L,0L,0L,0L);
        Set<String> sendSet = redisTemplate.keys(Nstatus.SendInfo.V+"*");
        sendSet.forEach(k->{
            Long tmpsendnum = getSendNumInfobase(k);
            Long tmpsenddata = getSendDataInfobase(k);
            sendRecInfo.setSendNum(sendRecInfo.getSendNum()+tmpsendnum);
            sendRecInfo.setSendData(sendRecInfo.getSendData()+tmpsenddata);
        });
        Set<String> recSet = redisTemplate.keys(Nstatus.RecInfo.V+"*");
        recSet.forEach(k->{
            Long tmprecnum = getRecNumInfobase(k);
            Long tmprecdata = getRecDataInfobase(k);
            sendRecInfo.setRecNum(sendRecInfo.getRecNum()+tmprecnum);
            sendRecInfo.setRecData(sendRecInfo.getRecData()+tmprecdata);
        });
        return sendRecInfo;
    }

    public void saveDataDetail(String msg,String address){
        redisTemplate.opsForList().leftPush(Nstatus.Msg_to_web.V,msg+Nstatus.Msg_middle.V+address);
    }

    public String[] getDataDetail(){
        Object object = redisTemplate.opsForList().leftPop(Nstatus.Msg_to_web.V);
        if(object==null){
            return  null;
        }
        return object.toString().split(Nstatus.Msg_middle.V);
    }


}
