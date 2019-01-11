package com.example.nettyclient;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class MyChannelPoolHandler implements ChannelPoolHandler {
    @Override
    public void channelReleased(Channel channel) throws Exception {
        System.out.println("channelReleased. Channel ID: " + channel.id());
        //释放channel
        //刷新掉管道的数据
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER);

    }

    //取channel

    @Override
    public void channelAcquired(Channel channel) throws Exception {
        System.out.println("channelAcquired. Channel ID: " + channel.id());
    }

    //创建channel

    @Override
    public void channelCreated(Channel channel) throws Exception {
        System.out.println("channelCreated. Channel ID: " + channel.id());
        SocketChannel ch = (SocketChannel) channel;
        ch.config().setKeepAlive(true)
                .setTcpNoDelay(true);
        ch.pipeline().addLast(new ClientHandler());

      /*  NioServerSocketChannel ch = (NioServerSocketChannel) channel;*/



    }
}
