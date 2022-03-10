package com.example.imclient;

import com.example.common.codec.ProtobufDecoder;
import com.example.common.codec.ProtobufEncoder;
import com.example.common.util.RedisUtil;
import com.example.imclient.handler.ChatMsgHandler;
import com.example.imclient.handler.ExceptionHandler;
import com.example.imclient.handler.LoginResponseHandler;
import com.example.imclient.util.ZkUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Slf4j
@Data
@Service
public class ChatClient {

	@Value("${zookeeper.host}")
	private String zkHost;

	@Value("${zookeeper.port}")
	private String zkPort;

	@Value("${redis.addr}")
	private String redisAddr;

	@Value("${redis.password}")
	private String redisPwd;

	@Autowired
	private LoginResponseHandler responseHandler;

	@Autowired
	private ExceptionHandler exceptionHandler;

	@Autowired
	private ChatMsgHandler chatMsgHandler;

	private GenericFutureListener<ChannelFuture> connectedListener;

	private EventLoopGroup eventLoopGroup;

	private Bootstrap bootstrap;

	private String[] serverInfo;

	ChatClient() {
		eventLoopGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
	}

	public void connect(String host, String port) {
		try {
			bootstrap.group(eventLoopGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			bootstrap.remoteAddress(host, Integer.parseInt(port));

			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast("decoder", new ProtobufDecoder());
					ch.pipeline().addLast("encoder", new ProtobufEncoder());
					ch.pipeline().addLast(chatMsgHandler);
					ch.pipeline().addLast(responseHandler);
					ch.pipeline().addLast(exceptionHandler);
				}
			});

			log.info("client start to connect the server!");

			ChannelFuture future = bootstrap.connect();
			future.addListener(connectedListener);

			future.channel().closeFuture().sync();
		} catch (Exception e) {
			log.error("client connect failed, " + e.getMessage());
		}
	}

	public void close() {
		eventLoopGroup.shutdownGracefully();
	}

	public void startup() {
		RedisUtil.connect(redisAddr, redisPwd);
		ZkUtil zkUtil = new ZkUtil();
		zkUtil.connect(zkHost, zkPort);

		try {
			serverInfo = zkUtil.getRandomServer();
			connect(serverInfo[0], serverInfo[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}
}
