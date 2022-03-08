package com.example.imclient.handler;

import com.example.common.bean.User;
import com.example.common.meta.Immsg;
import com.example.imclient.builder.HeartBeatBuilder;
import com.example.imclient.session.ClientSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@ChannelHandler.Sharable
public class HeartBeatHandler extends ChannelInboundHandlerAdapter {

	// timeunit : seconds
	private static final int HEARTBEAT_INTERVAL = 100;

	/**
	 * 在Handler被加入pipeline时，开始发送心跳
	 * 然后每100秒递归发送心跳信息
	 * @param ctx ChannelHandlerContext
	 * @throws Exception Exception
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		ClientSession session = ClientSession.getSession(ctx);
		User user = session.getUser();
		HeartBeatBuilder builder = new HeartBeatBuilder(user, session);
		Immsg.Message message = builder.buildMsg();
		heartbeat(ctx, message);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof Immsg.Message)) {
			super.channelRead(ctx, msg);
			return;
		}

		Immsg.Message pkg = (Immsg.Message) msg;
		Immsg.HeadType headType = pkg.getType();
		if (headType.equals(Immsg.HeadType.HEART_BEAT)) {
			log.info("receive heartbeat msg from server!");
			return;
		} else {
			super.channelRead(ctx, msg);
		}
	}

	/**
	 * 定时器发送心跳数据包
	 * @param ctx ChannelHandlerContext
	 * @param heartBeatMsg Immsg.Message
	 */
	public void heartbeat(ChannelHandlerContext ctx, Immsg.Message heartBeatMsg) {
		ctx.executor().schedule(() -> {
			if (ctx.channel().isActive()) {
				log.info("send HeartBeat msg to server");
				ctx.writeAndFlush(heartBeatMsg);
				heartbeat(ctx, heartBeatMsg);
			}
		}, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
	}


}
