package com.example.imserver;

import com.example.common.codec.ProtobufDecoder;
import com.example.common.codec.ProtobufEncoder;
import com.example.imserver.handler.ChatRedirectHandler;
import com.example.imserver.handler.HeartBeatServerHandler;
import com.example.imserver.handler.LoginRequestHandler;
import com.example.imserver.handler.ServerExceptionHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetSocketAddress;

@Slf4j
@Service("ChatServer")
public class ChatServer {

	@Value("${server.port}")
	private int port;

	@Autowired
	private LoginRequestHandler loginRequestHandler;

	@Autowired
	private ChatRedirectHandler chatRedirectHandler;

	@Autowired
	private ServerExceptionHandler serverExceptionHandler;

	@PostConstruct
	public void run() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();

		try {
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.localAddress(new InetSocketAddress(port));
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);

			bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ProtobufDecoder());
					ch.pipeline().addLast(new ProtobufEncoder());
					ch.pipeline().addLast(new HeartBeatServerHandler());
					ch.pipeline().addLast(loginRequestHandler);
					ch.pipeline().addLast(chatRedirectHandler);
					ch.pipeline().addLast(serverExceptionHandler);
				}
			});
			// 绑定端口
			ChannelFuture future = bootstrap.bind().sync();
			log.info("IM server start at port: " + port);
			// 关闭
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}


	}

}
