package com.example.imclient.sender;

import com.example.common.bean.User;
import com.example.common.meta.Msg;
import com.example.imclient.session.ClientSession;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public abstract class BaseSender {

	// 用来发送数据包

	private User user;

	private ClientSession clientSession;

	public boolean isConnected() {
		if (null == clientSession) {
			log.info("session is null");
			return false;
		}
		return clientSession.isConnectState();
	}

	public boolean isLogin() {
		if (null == clientSession) {
			log.info("session is null");
			return false;
		}
		return clientSession.isLoginState();
	}

	public void sendMessage(Msg.ProtoMsg.Message message) {
		if (null == getClientSession() || !isConnected()) {
			log.info("connection is still not established!");
			return;
		}

		Channel channel = clientSession.getChannel();
		ChannelFuture future = channel.writeAndFlush(message);
		//  ChannelFuture有结果了会在addListener中执行operationComplete方法
		future.addListener((ChannelFutureListener) future1 -> {
			if (future1.isSuccess()) {
				sendSucceed(message);
			} else {
				sendFailed(message);
			}
		});

		try {
			future.sync();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}
	}

	protected void sendSucceed(Msg.ProtoMsg.Message msg) {
		log.info("send message successfully!");
	}

	protected void sendFailed(Msg.ProtoMsg.Message msg) {
		log.warn("send message failed!");
	}


}
