package com.example.nettyserver;

import java.net.SocketAddress;
import java.time.LocalDateTime;

import com.example.conf.SpringContextUtils;
import com.example.entity.ClientOnAndOffline;
import com.example.entity.NettyMessage;
import com.example.nettywebsocket.WebSocketUtil;
import com.example.service.KeepDateService;
import com.example.service.NettyMsgService;
import com.example.service.OnAndOfflineService;
import com.example.unit.Nstatus;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ServerHandler extends ChannelInboundHandlerAdapter{


	private NettyMessage nettyMessage;

	private NettyMsgService msgService = (NettyMsgService) SpringContextUtils.getBean(NettyMsgService.class);

	private KeepDateService keepDateService = (KeepDateService) SpringContextUtils.getBean(KeepDateService.class);

	private OnAndOfflineService lineService = (OnAndOfflineService) SpringContextUtils.getBean(OnAndOfflineService.class);


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		SocketAddress socketAddress = ctx.channel().remoteAddress();
		System.out.println(socketAddress+"连接到服务器");
		nettyMessage = new NettyMessage(socketAddress.toString(),Nstatus.Online.V,Nstatus.CloseWebSocket.V);
		msgService.addNettyServerMsg(socketAddress,nettyMessage);
		ServerUtil.getInstance().addChannelToMap(socketAddress.toString(),ctx.channel());
		lineService.saveClientInfo(socketAddress.toString(),new ClientOnAndOffline(socketAddress.toString(),Nstatus.Online.V, LocalDateTime.now()));
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		SocketAddress socketAddress = ctx.channel().remoteAddress();
		System.out.println(socketAddress+"断开连接");
		nettyMessage.setStatus(Nstatus.OffLine.V);
		msgService.addNettyServerMsg(socketAddress,nettyMessage);
		ServerUtil.getInstance().removeChannel(socketAddress.toString());
		lineService.saveClientInfo(socketAddress.toString(),new ClientOnAndOffline(socketAddress.toString(),Nstatus.OffLine.V, LocalDateTime.now()));
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			ByteBuf buf = (ByteBuf) msg;
			byte[] bytes = new byte[buf.readableBytes()];
			buf.readBytes(bytes);
			String result = new String(bytes,"UTF-8");
	//		System.out.println("接收的数据："+result);
			//保存接受的数据长度
			keepDateService.recNumRise(ctx.channel().remoteAddress().toString(), (long) bytes.length);

			keepDateService.saveDataDetail(result,ctx.channel().remoteAddress().toString());
	//		WebSocketUtil.getInstance().sendMsg(result,ctx.channel().remoteAddress().toString());




			//返回同样数据

			//如果以string 发送会乱码
	//		ServerUtil.getInstance().sendMsg(result,ctx.channel().remoteAddress().toString());
	//		ServerUtil.getInstance().sendMsg(bytes,ctx.channel().remoteAddress().toString());
			ReferenceCountUtil.release(msg);//释放内存

		
		
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();

	}


	

}
