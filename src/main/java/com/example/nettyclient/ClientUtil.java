package com.example.nettyclient;

import com.example.conf.SpringContextUtils;
import com.example.entity.NettyMessage;
import com.example.nettywebsocket.WebSocketUtil;
import com.example.service.KeepDateService;
import com.example.service.NettyMsgService;
import com.example.unit.Nstatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientUtil {

    private ClientUtil(){}
    private static final class SingleHolder{
        private static final ClientUtil INSTANCE = new ClientUtil();
    }
    public static ClientUtil getInstance(){
        return SingleHolder.INSTANCE;
    }

    private Map<String,Channel> channelsMap = new ConcurrentHashMap<>();

    private ClientPool pool; //client连接池 最大10个

//    private Client client; //create client

    private KeepDateService keepDateService = (KeepDateService) SpringContextUtils.getBean(KeepDateService.class);

    private NettyMsgService msgService = (NettyMsgService) SpringContextUtils.getBean(NettyMsgService.class);

    private boolean isFirst=true;//是否第一次创建client

    private Logger logger = LoggerFactory.getLogger(ClientUtil.class);

    /**
     * 端口 ip client Num
     * @param
     * @param
     * @param
     */
    //--- clientpool 连接池方式 缓存channel
   public void startClient(int port,String host,int num){
        if(isFirst){
            pool = new ClientPool(port,host);
            isFirst=false;
        }
        addClient(num);
    }

    public void addClient(int num){
        if(pool==null){
            return;
        }
        for(int i=0;i<num;i++){
           Channel channel =  pool.getChannel();
            logger.info("channel:"+channel);
           if(channel!=null){
           //    channels.add(channel);
               channelsMap.put(channel.localAddress().toString(),channel);
               msgService.addNettyMsg(channel.localAddress(),new NettyMessage(channel.localAddress().toString(),Nstatus.Online.V,Nstatus.CloseWebSocket.V));
           }

        }
    }

    public void closeClient(){
        if(channelsMap==null||channelsMap.isEmpty()){
            return;
        }
        channelsMap.forEach((k,v)->endclose(v));
        channelsMap.clear();
    }
    public void closeClient(SocketAddress address){
        if(channelsMap==null||channelsMap.isEmpty()){
            return;
        }
        endclose(channelsMap.remove(address.toString()));
    }

    /**
     * 释放连接 不关闭channel
     * @param channel
     */
    public void endclose(Channel channel){
        pool.releaseChannel(channel);
        msgService.addNettyMsg(channel.localAddress(),new NettyMessage(channel.localAddress().toString(),Nstatus.OffLine.V,Nstatus.CloseWebSocket.V));
    }


    /*public void endclose(Channel channel){
        channel.close();
        msgService.addNettyMsg(channel.localAddress(),new NettyMessage(channel.localAddress().toString(),Nstatus.OffLine.V,Nstatus.CloseWebSocket.V));
    }*/

//---client 不缓存channel  --


  /*  public void startClient(int port,String host,int num){
        if(isFirst){
            client = new Client();

            isFirst=false;
        }
        addClient(port,host,num);
    }


    public void addClient(int port,String host,int num){
        if(client==null){
            return;
        }

            Set<Channel> channels = null;
            try {
                channels = client.getChannel(new InetSocketAddress(host,port),num);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            channels.forEach(channel -> {
                logger.info("channel:"+channel);
                if(channel!=null){
                    //    channels.add(channel);
                    channelsMap.put(channel.localAddress().toString(),channel);
                    msgService.addNettyMsg(channel.localAddress(),new NettyMessage(channel.localAddress().toString(),Nstatus.Online.V,Nstatus.CloseWebSocket.V));
                }
            });



    }*/











    public void sendMsg(String msg){
        System.out.println("sendMsg:"+msg);
        if(channelsMap==null||channelsMap.isEmpty()){
            return;
        }
        channelsMap.forEach((k,channel) -> {
            writeMsg(channel,msg);

        });
    }

    public void sendMsg(String msg,String socketaddress){
        System.out.println("sendMsg:"+msg+"socketaddress:"+socketaddress);
        if(channelsMap==null||channelsMap.isEmpty()){
            return;
        }
        Channel channel = channelsMap.get(socketaddress);
        if(channel==null)return;
        writeMsg(channel,msg);
    }

    public void writeMsg(Channel channel,String msg){
        try {
            byte[] bytes = msg.getBytes("UTF-8");
            ByteBuf byteBuf = Unpooled.buffer(bytes.length);
            byteBuf.writeBytes(bytes);
            channel.writeAndFlush(byteBuf);
            keepDateService.sendNumRise(channel.localAddress().toString(), (long) bytes.length);
        //    WebSocketUtil.getInstance().sendMsg(msg,channel.localAddress().toString());
            byteBuf.release();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public Integer getChannelMapSize(){
        return channelsMap.size();
    }

}
