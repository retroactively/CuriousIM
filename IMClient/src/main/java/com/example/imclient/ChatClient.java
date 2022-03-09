package com.example.imclient;

import com.example.common.codec.ProtobufDecoder;
import com.example.common.codec.ProtobufEncoder;
import com.example.imclient.handler.ChatMsgHandler;
import com.example.imclient.handler.ExceptionHandler;
import com.example.imclient.handler.LoginResponseHandler;
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

	@Value("${server.ip}")
	private String host;

	@Value("${server.port}")
	private int port;

	@Autowired
	private LoginResponseHandler responseHandler;

	@Autowired
	private ExceptionHandler exceptionHandler;

	@Autowired
	private ChatMsgHandler chatMsgHandler;

	private GenericFutureListener<ChannelFuture> connectedListener;

	private EventLoopGroup eventLoopGroup;

	private Bootstrap bootstrap;

	ChatClient() {
		eventLoopGroup = new NioEventLoopGroup();
		bootstrap = new Bootstrap();
	}

	public void connect() {
		try {
			bootstrap.group(eventLoopGroup);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
			bootstrap.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT);
			bootstrap.remoteAddress(host, port);

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

}
