package com.example.nettyclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashSet;
import java.util.Set;

public class Client {




    private Integer port;
    private String host;

    private Bootstrap bootstrap=null;
    public Client(){
        start();
    }

    private Logger logger = LoggerFactory.getLogger(Client.class);

    public Bootstrap start() {
        logger.info("client create");
            if(bootstrap==null){

                EventLoopGroup loopGroup = new NioEventLoopGroup();
                bootstrap = new Bootstrap();
                bootstrap.group(loopGroup)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY,true)
                        .option(ChannelOption.SO_KEEPALIVE,true)
                        .handler(new ClientHandler());
            }
        logger.info("bootstrap: "+bootstrap);
        return bootstrap;
    }

    public Set<Channel> getChannel(SocketAddress socketAddress,int num) throws InterruptedException {
        Set<Channel> channels =new HashSet<>();
        for(int i=0;i<num;i++) {
            ChannelFuture future = bootstrap.connect(socketAddress).sync();
            channels.add(future.channel());
        }
        return channels;
    }

   /* public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        for(int i=0;i<10;i++){
            System.out.println("channel:"+client.getChannel(new InetSocketAddress("127.0.0.1",60000)));
        }
    }*/
}
