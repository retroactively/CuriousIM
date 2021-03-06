package com.example.imserver.handler;

import com.example.common.concurrent.FutureTaskScheduler;
import com.example.common.meta.Immsg;
import com.example.imserver.session.ServerSession;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartBeatServerHandler extends IdleStateHandler {

	private static final int READ_IDLE_GAP = 150;

	public HeartBeatServerHandler() {
		super(READ_IDLE_GAP, 0, 0, TimeUnit.SECONDS);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 1. 无效消息，后传
		if (!(msg instanceof Immsg.Message)) {
			super.channelRead(ctx, msg);
			return;
		}

		Immsg.Message pkg = (Immsg.Message) msg;
		Immsg.HeadType headType = pkg.getType();
		if (headType.equals(Immsg.HeadType.HEART_BEAT)) {
			// 异步处理，心跳包改为KEEPALIVE_RESPONSE
			FutureTaskScheduler.add(()-> {
				if (ctx.channel().isActive()) {
					log.info("get HEART_BEAT message from client");
					ctx.writeAndFlush(msg);
				}
			});
		}
		super.channelRead(ctx, msg);

	}

	@Override
	protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
		log.warn("not read data in " + READ_IDLE_GAP + " seconds, close the connection");
		ServerSession.closeSession(ctx);
	}
}
