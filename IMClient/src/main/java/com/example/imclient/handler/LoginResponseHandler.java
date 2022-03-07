package com.example.imclient.handler;

import com.example.common.meta.Immsg;
import com.example.common.meta.ProtoInstant;
import com.example.imclient.session.ClientSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class LoginResponseHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 1、 判断消息实例是否合法
		if (!(msg instanceof Immsg.Message)) {
			super.channelRead(ctx, msg);
			return;
		}
		// 2、判断消息类型
		Immsg.Message message = (Immsg.Message)msg;
		if (!message.getType().equals(Immsg.HeadType.LOGIN_RESPONSE)) {
			super.channelRead(ctx, msg);
			return;
		}
		// 3、判断响应码是否正确
		Immsg.LoginResponse response = message.getLoginResponse();
		ProtoInstant.ResultCodeEnum res = ProtoInstant.ResultCodeEnum.values()[response.getCode()];
		if (!(res.equals(ProtoInstant.ResultCodeEnum.SUCCESS))) {
			// 4、响应码错误则记录
			log.warn(res.getDesc());
		} else {
			// 5、 响应码正确则登陆成功，添加心跳机制
			ClientSession.loginSuccess(ctx, message);
			ChannelPipeline pipeline = ctx.pipeline();
			// 移除登陆响应处理器
			pipeline.remove(this);
			// 在编码器后面新增心跳机制处理器
			pipeline.addAfter("encoder", "heartbeat", new HeartBeatHandler());
		}
	}
}
