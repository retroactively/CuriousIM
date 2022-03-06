package com.example.imserver.handler;

import com.example.common.concurrent.CallbackTask;
import com.example.common.concurrent.CallbackTaskScheduler;
import com.example.common.meta.Immsg;
import com.example.imserver.processor.LoginProcessor;
import com.example.imserver.session.ServerSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class LoginRequestHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private LoginProcessor loginProcessor;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 1. 无效消息，后传
		if (!(msg instanceof Immsg.Message)) {
			super.channelRead(ctx, msg);
			return;
		}

		Immsg.Message pkg = (Immsg.Message) msg;
		Immsg.HeadType headType = pkg.getType();
		// 2. 非登陆消息，后传
		if (!headType.equals(loginProcessor.getType())) {
			super.channelRead(ctx, msg);
			return;
		}

		ServerSession session = new ServerSession(ctx.channel());

		CallbackTaskScheduler.add(new CallbackTask<Boolean>() {

			@Override
			public Boolean execute() throws Exception {
				return loginProcessor.action(session, pkg);
			}

			// 异步任务返回
			@Override
			public void onSuccess(Boolean flag) {
				if (flag) {
					// success
					ctx.pipeline().remove(LoginRequestHandler.this);
					log.info("login successfully, user : " + session.getUser());
				} else {
					ServerSession.closeSession(ctx);
					log.info("login failed, user : " + session.getUser());
				}
			}

			// 异步任务异常
			@Override
			public void onFailure(Throwable throwable) {
				ServerSession.closeSession(ctx);
				log.info("login failed, use : " + session.getUser());
			}
		});
	}
}
