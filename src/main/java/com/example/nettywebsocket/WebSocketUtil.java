package com.example.nettywebsocket;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.WebSocketInfo;
import com.example.unit.Nstatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketUtil {

    private Map<String, Channel> websocketMap = new ConcurrentHashMap<>();

    private Set<Channel> channels = new LinkedHashSet<>();

    private WebSocketServer webSocketServer;

    private boolean flag = true;

    private WebSocketUtil(){}

    private static final class SingleHolder{
        private static  final WebSocketUtil INSTANCE = new WebSocketUtil();
    }

    public static WebSocketUtil getInstance(){
        return SingleHolder.INSTANCE;
    }

    public void addwebsocket(String ip,Channel channel){
        /*if(flag){
            flag = false;*/
        //    websocketMap.put(ip,channel);
            channels.add(channel);
       /* }*/

    }
    public void removewebsocket(Channel channel){

        channels.remove(channel);

      /*  websocketMap.forEach((k,v)->{
            if(v==channel){
                websocketMap.remove(k);
            }
        });*/
    }

    public void sendMsg(String msg,String ip){

        channels.forEach(channel -> sendMsg(channel,msg,ip));


    }

    public void sendMsg(Channel channel,String msg,String socketAddress){
        try {
           /* byte[] bytes = msg.getBytes("UTF-8");
            ByteBuf byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            channel.writeAndFlush(byteBuf);*/
            WebSocketInfo webSocketInfo = new WebSocketInfo(socketAddress, Nstatus.Client.V,msg, LocalDateTime.now());
            channel.writeAndFlush(new TextWebSocketFrame(JSONObject.toJSONString(webSocketInfo)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startWebService(){
        webSocketServer = new WebSocketServer();
        new Thread(()->{
            webSocketServer.startServer(8888);
        }).start();

    }

    public void stopWebService(){
        webSocketServer.stopWebsocketServer();
    }
}
