package com.example.imclient.handler;

import com.example.common.exception.InvalidFrameException;
import com.example.imclient.session.ClientSession;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ExceptionHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.error(cause.getMessage());
		if (cause instanceof InvalidFrameException) {
			// 协议错误，直接关闭会话
			ClientSession.getSession(ctx).close();
		} else {
			// 其他错误，尝试重新连接
			ctx.close();
			// TODO reconnect
		}
	}
}
