package com.example.nettyclient;

import com.example.conf.SpringContextUtils;
import com.example.entity.NettyMessage;
import com.example.nettywebsocket.WebSocketUtil;
import com.example.service.KeepDateService;
import com.example.service.NettyMsgService;
import com.example.unit.Nstatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.EventExecutorGroup;

import java.net.SocketAddress;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    private NettyMessage nettyMessage;

    private NettyMsgService msgService = (NettyMsgService) SpringContextUtils.getBean(NettyMsgService.class);

    private KeepDateService keepDateService = (KeepDateService) SpringContextUtils.getBean(KeepDateService.class);

    public ClientHandler(){
        nettyMessage = new NettyMessage();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().localAddress();
        System.out.println(socketAddress+"连接到服务器");


    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketAddress socketAddress = ctx.channel().localAddress();
        System.out.println(socketAddress+"断开连接");
        ClientUtil.getInstance().closeClient(socketAddress);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        String result = new String(bytes,"UTF-8");
     //   System.out.println("接收的数据："+result);
        //保存接受的数据长度
        keepDateService.recNumRise(ctx.channel().localAddress().toString(), (long) bytes.length);
        keepDateService.saveDataDetail(result,ctx.channel().localAddress().toString());
    //    WebSocketUtil.getInstance().sendMsg(result,ctx.channel().localAddress().toString());
        ReferenceCountUtil.release(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println(ctx.channel().localAddress()+"异常 cause："+cause);
        cause.printStackTrace();
        ctx.close();

    }
}
