package com.example.nettywebsocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

public class WebSocketServer {

    private EventLoopGroup bossgroup;
    private EventLoopGroup workGroup;


    public WebSocketServer(){
            bossgroup = new NioEventLoopGroup();
            workGroup = new NioEventLoopGroup();
    }


    public void startServer(int port){
        ServerBootstrap b = new ServerBootstrap();
        b.group(bossgroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast("httpServerCodec",new HttpServerCodec())
                                    .addLast("chunkWiterHandler",new ChunkedWriteHandler())
                                    .addLast("httpObjectAggregator",new HttpObjectAggregator(8192))
                                    .addLast("myWebSocketHandler",new WebSocketHandler());
                    }
                });
        try {
           Channel channel = b.bind(port).sync().channel();
           channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void stopWebsocketServer(){
        bossgroup.shutdownGracefully();
        workGroup.shutdownGracefully();
    }

}
