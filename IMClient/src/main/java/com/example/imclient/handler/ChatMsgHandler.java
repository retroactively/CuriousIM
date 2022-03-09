package com.example.imclient.handler;

import com.example.common.meta.Immsg;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@ChannelHandler.Sharable
@Service
public class ChatMsgHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		if (!(msg instanceof Immsg.Message)) {
			super.channelRead(ctx, msg);
			return;
		}

		Immsg.Message pkg = (Immsg.Message) msg;
		if (!pkg.getType().equals(Immsg.HeadType.MESSAGE_REQUEST)) {
			super.channelRead(ctx, msg);
			return;
		}

		Immsg.MessageRequest request = pkg.getMessageRequest();
		log.info(" receive msg from uid: " + request.getFrom() + " -> " + request.getContent());
	}
}
