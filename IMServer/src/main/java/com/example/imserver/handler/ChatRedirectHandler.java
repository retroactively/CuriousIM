package com.example.imserver.handler;


import com.example.common.concurrent.FutureTaskScheduler;
import com.example.common.meta.Immsg;
import com.example.imserver.processor.ChatRedirectProcessor;
import com.example.imserver.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ChatRedirectHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private ChatRedirectProcessor processor;


	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof Immsg.Message)) {
			super.channelRead(ctx, msg);
			return;
		}

		Immsg.Message pkg = (Immsg.Message) msg;
		if (!pkg.getType().equals(processor.getType())) {
			super.channelRead(ctx, msg);
			return;
		}

		ServerSession session = ServerSession.getSession(ctx);
		if (null == session || !session.isLoginState()) {
			log.error("user not login, can not send msg.");
			return;
		}

		// 将处理任务加入工作池
		FutureTaskScheduler.add(() -> processor.action(session, pkg));


	}
}
