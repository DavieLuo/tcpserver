package com.example.nettywebsocket;

import com.alibaba.fastjson.JSONObject;
import com.example.entity.WebSocketInfo;
import com.example.nettyclient.ClientUtil;
import com.example.nettyserver.ServerUtil;
import com.example.unit.Nstatus;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.UnsupportedEncodingException;


public class WebSocketHandler extends ChannelInboundHandlerAdapter {

    private WebSocketServerHandshaker handshaker;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        WebSocketUtil.getInstance().removewebsocket(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

            if(msg instanceof FullHttpRequest){
                handlerHttpMsg(ctx, (FullHttpRequest) msg);
            }
            if(msg instanceof WebSocketFrame){
                handlerWebSocketFrame(ctx, (WebSocketFrame) msg);
            }

        ReferenceCountUtil.release(msg);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
    }

    public void handlerHttpMsg(ChannelHandlerContext ctx,FullHttpRequest request){
        if(request.decoderResult().isSuccess()&&"websocket".equals(request.headers().get("Upgrade"))){
            System.out.println("create websocket connect");
            WebSocketServerHandshakerFactory factory = new WebSocketServerHandshakerFactory("ws://localhost:8888/websocket", null, false);
            handshaker = factory.newHandshaker(request);
            if(handshaker==null){
                WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            }else{
                handshaker.handshake(ctx.channel(),request);
                WebSocketUtil.getInstance().addwebsocket("s",ctx.channel());
            }
            return;
        }
        sendHttpResponse(ctx, request,new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
    }

    public void handlerWebSocketFrame(ChannelHandlerContext ctx,WebSocketFrame frame) throws UnsupportedEncodingException {

            // 判断是否是关闭链路的指令
            if (frame instanceof CloseWebSocketFrame) {
                handshaker.close(ctx.channel(),(CloseWebSocketFrame) frame.retain());
                return;
            }
            // 判断是否是Ping消息
            if (frame instanceof PingWebSocketFrame) {
                ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
                return;
            }
            // 本例程仅支持文本消息，不支持二进制消息
            if (!(frame instanceof TextWebSocketFrame)) {
                throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
            }

            ByteBuf byteBuf = frame.content();
            byte[] req = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(req);

            String s= new String(req,"UTF-8");
            WebSocketInfo webSocketInfo = JSONObject.parseObject(s, WebSocketInfo.class);

            System.out.println("websockt 登入ip："+ctx.channel().remoteAddress());

            //发送数据给模块
            snedMsgtoModule(webSocketInfo);
    }




    private static void sendHttpResponse(ChannelHandlerContext ctx,FullHttpRequest req, FullHttpResponse res) {
        try {
            // 返回应答给客户端
            if (res.status().code() != 200) {
                ByteBuf buf = Unpooled.copiedBuffer(res.status().toString(), CharsetUtil.UTF_8);
                res.content().writeBytes(buf);
                buf.release();
            }
            // 如果是非Keep-Alive，关闭连接
			ChannelFuture f = ctx.channel().writeAndFlush(res);
//			if (!isKeepAlive(req) || res.status().code() != 200) {
//				f.addListener(ChannelFutureListener.CLOSE);
//			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void snedMsgtoModule(WebSocketInfo webSocketInfo){
        if(webSocketInfo.getType()==null) return;
        if(webSocketInfo.getType().equals(Nstatus.Server.V)){
            ServerUtil.getInstance().sendMsg(webSocketInfo.getMsg(),webSocketInfo.getSocketAddress());
        }else if(webSocketInfo.getType().equals(Nstatus.Client.V)){
            ClientUtil.getInstance().sendMsg(webSocketInfo.getMsg(),webSocketInfo.getSocketAddress());
        }else{

        }
    }
}
