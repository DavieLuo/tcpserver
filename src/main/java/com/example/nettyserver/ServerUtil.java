package com.example.nettyserver;


import com.example.conf.SpringContextUtils;
import com.example.service.KeepDateService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.CharsetUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServerUtil {

    private ServerUtil(){}
    private static final class Singleholder {
        private static final ServerUtil INSTANCE = new ServerUtil();
    }
    public static ServerUtil getInstance(){
        return Singleholder.INSTANCE;
    }

    private Map<String, Channel> map = new ConcurrentHashMap<>();

    private KeepDateService keepDateService = (KeepDateService) SpringContextUtils.getBean(KeepDateService.class);

    private NettyService nettyService;


    public void addChannelToMap(String socketAddress,Channel channel){
        map.put(socketAddress,channel);
    }

    public Channel getChannel(String socketAddress){
        return map.get(socketAddress);
    }

    public Channel removeChannel(String socketAddress){
        return map.remove(socketAddress);
    }

    public Channel removeChannel(Channel channel){
        return map.remove(channel.remoteAddress().toString());
    }

    public void clearChannel(){
        map.forEach((k,v)->v.close());
        map.clear();
    }

    public void sendMsg(String msg,String socketAddress){
        Channel channel = getChannel(socketAddress);
        if(channel==null)return;
        writeMsg(channel,msg);
    }

    public void sendMsg(byte[] msg,String socketAddress){
        Channel channel = getChannel(socketAddress);
        if(channel==null)return;
        writeMsg(channel,msg);
    }

    public void sendMsgAll(String msg){
        map.forEach((k,channel)-> writeMsg(channel,msg));
    }

    public void writeMsg(Channel channel,String msg){
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        channel.writeAndFlush(byteBuf);

        keepDateService.sendNumRise(channel.remoteAddress().toString(), (long) msg.length());
    }
    public void writeMsg(Channel channel,byte[] msg){
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg);
        channel.writeAndFlush(byteBuf);
        keepDateService.sendNumRise(channel.remoteAddress().toString(), (long) msg.length);
        byteBuf.release();
    }

    public String startService(Integer port){
        if(nettyService==null){
            nettyService = new NettyService();
            new Thread(()->nettyService.start(port)).start();
            return "OK";
        }else {
            return "启动失败：已经启动一个服务";
        }
    }

    public void stopService(){
        if(nettyService!=null){
            nettyService.stop();
            nettyService=null;
            map.clear();
        }
    }


}
