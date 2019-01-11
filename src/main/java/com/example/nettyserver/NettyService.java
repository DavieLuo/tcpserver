package com.example.nettyserver;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.util.Strings;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;

/**
 * tcp server
 */
public class NettyService {
	
	private EventLoopGroup boss = null;
	private EventLoopGroup worker =null;
	
	public NettyService() {
		 boss = new NioEventLoopGroup();
		 worker = new NioEventLoopGroup();
	}
	
	public void start(Integer port) {
		
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		
		serverBootstrap.group(boss,worker);
		
		serverBootstrap.channel(NioServerSocketChannel.class);
		
		serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
		
		serverBootstrap.option(ChannelOption.ALLOCATOR,PooledByteBufAllocator.DEFAULT); 
		
		serverBootstrap.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.TCP_NODELAY, true);
		
		serverBootstrap.childHandler(new ChannelInitializer<Channel>() {

			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline =ch.pipeline();
				pipeline.addLast("idleStateHandler", new IdleStateHandler(60,0, 0, TimeUnit.SECONDS))
					.addLast(new ServerHandler());
				
			}
			
			
		});
		
		try {
			ChannelFuture future =serverBootstrap.bind(port).sync();
			
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
			stop();
		}
	}
	
	public void stop() {
		boss.shutdownGracefully();
		worker.shutdownGracefully();
	}
	
	
	
}
