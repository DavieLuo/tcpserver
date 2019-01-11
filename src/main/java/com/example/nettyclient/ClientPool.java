package com.example.nettyclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.pool.FixedChannelPool;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;

public class ClientPool {

    private  FixedChannelPool channelPool=null;

    public ClientPool(){}
    public ClientPool(int port,String host){
        connect(port,host,10);
    }


    private void connect(int port,String host,int maxChannel){
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        InetSocketAddress socketAddress = new InetSocketAddress(host,port);
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY,true)
                .remoteAddress(socketAddress);

        channelPool = new FixedChannelPool(bootstrap,new MyChannelPoolHandler(),maxChannel);

    }

    public Channel getChannel(){

        try {
            return this.channelPool.acquire().get();//.addListener(new ConnectListener())
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void releaseChannel(Channel channel){
        this.channelPool.release(channel);
    }


}
