package com.example.imserver;

import com.example.common.codec.ProtobufDecoder;
import com.example.common.codec.ProtobufEncoder;
import com.example.imserver.handler.ChatRedirectHandler;
import com.example.imserver.handler.HeartBeatServerHandler;
import com.example.imserver.handler.LoginRequestHandler;
import com.example.imserver.handler.ServerExceptionHandler;
import com.example.imserver.util.ZkUtil;
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

	@Value("${server.host}")
	private String host;

	@Value("${server.port}")
	private String serverPort;

	@Value("${zookeeper.port}")
	private String zkPort;

	@Autowired
	private LoginRequestHandler loginRequestHandler;

	@Autowired
	private ChatRedirectHandler chatRedirectHandler;

	@Autowired
	private ServerExceptionHandler serverExceptionHandler;

	private ZkUtil zkUtil;

	@PostConstruct
	public void run() {
		ServerBootstrap bootstrap = new ServerBootstrap();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workGroup = new NioEventLoopGroup();
		zkUtil = new ZkUtil();

		try {
			bootstrap.group(bossGroup, workGroup);
			bootstrap.channel(NioServerSocketChannel.class);
			bootstrap.localAddress(new InetSocketAddress(Integer.parseInt(serverPort)));
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
			ChannelFuture future = bootstrap.bind().sync().addListener(zkUtil.getZKFutureListener(host, zkPort));
			log.info("server start successfully at: " + host + ":" + serverPort);
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
