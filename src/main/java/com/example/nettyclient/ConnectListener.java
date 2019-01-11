package com.example.nettyclient;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

public class ConnectListener implements  GenericFutureListener<Future<? super Void>> {

    private Client client;
    public ConnectListener(Client client) {
        this.client = client;
    }

    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        if(!future.isSuccess()){
            System.out.println("重连");
            Channel channel = (Channel) future.getNow();
            try {
                client.getChannel(channel.remoteAddress(),1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /*@Override
    public void operationComplete(Future<? super Channel> future) {
        if(!future.isSuccess()){
            System.out.println("重连");
            Channel channel = (Channel) future.getNow();
            try {
                client.getChannel(channel.remoteAddress());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }*/
}
