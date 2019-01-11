package com.example.service;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.NettyMessage;
import com.example.unit.Nstatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;
import sun.nio.ch.Net;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NettyMsgService {


    @Autowired
    private RedisTemplate redisTemplate;



    public void addNettyMsg(SocketAddress address, NettyMessage nettyMessage){
        redisTemplate.opsForValue().set(Nstatus.NettyList.V+address.toString(), JSONObject.toJSONString(nettyMessage));
    }

    public NettyMessage getNettyMsg(SocketAddress address){
        return getNettyMsg(Nstatus.NettyList.V+address.toString());
    }

    public NettyMessage getNettyMsg(String key){
        NettyMessage nettyMessage =null;
        String nettymsg = redisTemplate.opsForValue().get(key).toString();
        if(!StringUtils.isEmpty(nettymsg)){
            nettyMessage=JSONObject.parseObject(nettymsg,NettyMessage.class);
        }
        return nettyMessage;
    }


    public void pushNettyMsgtoSet(NettyMessage nettyMessage){
        redisTemplate.opsForSet().add(Nstatus.NettyList.V, JSONObject.toJSONString(nettyMessage));
    }

    public Set<NettyMessage> queryAllNettyMsgSet(){
        Set<String> strings = redisTemplate.opsForSet().members(Nstatus.NettyList.V);
        Set<NettyMessage> nettyMessages = new HashSet<>();
        if(strings==null||strings.isEmpty()){
            return null;
        }
        strings.forEach(k->{
            NettyMessage nettyMessage = JSONObject.parseObject(k,NettyMessage.class);
            nettyMessages.add(nettyMessage);
        });
        return nettyMessages;
    }

    /**
     * 获取client列表
     * @return
     */
    public List<NettyMessage> queryAllNettyMsgList(){
        Set<String> strings = redisTemplate.keys(Nstatus.NettyList.V+"*");
        List<NettyMessage> nettyMessages = new ArrayList<>();
        System.out.println("strings:"+strings);
        if(strings==null||strings.isEmpty()){
            return nettyMessages;
        }
        strings.forEach(k->nettyMessages.add(getNettyMsg(k)));
        return nettyMessages;
    }


    /**
     * 程序为 tcp server 时 添加连接信息
     * @param address
     * @param nettyMessage
     */
    public void addNettyServerMsg(SocketAddress address, NettyMessage nettyMessage){
        redisTemplate.opsForValue().set(Nstatus.NettyServerList.V+address.toString(), JSONObject.toJSONString(nettyMessage));
    }

    /**
     * 程序为 tcp server 时 client 列表
     * @return
     */
    public List<NettyMessage> queryAllNettyServerMsgList(){
        Set<String> strings = redisTemplate.keys(Nstatus.NettyServerList.V+"*");
        List<NettyMessage> nettyMessages = new ArrayList<>();
        if(strings==null||strings.isEmpty()){
            return nettyMessages;
        }
        strings.forEach(k->nettyMessages.add(getNettyMsg(k)));
        return nettyMessages;
    }
}
