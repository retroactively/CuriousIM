package com.example.imclient.session;

import com.example.common.bean.UserDTO;
import com.example.common.meta.Msg;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Data
public class ClientSession {

	public static final AttributeKey<ClientSession> SESSION_KEY = AttributeKey.valueOf("SESSION_KEY");

	// 客户端用户会话管理的核心
	private Channel channel;

	private UserDTO user;

	// 保存登陆后，服务端返回的sessionId
	private String sessionId;

	private boolean connectState = false;

	private boolean loginState = false;

	/**
	 * session中存储的session 变量属性值
	 */
	private Map<String, Object> map = new HashMap<>();

	/**
	 * 绑定通道
	 * @param channel Channel
	 */
	public ClientSession(Channel channel) {
		this.channel = channel;
		this.sessionId = String.valueOf(-1);
		channel.attr(ClientSession.SESSION_KEY).set(this);
	}

	/**
	 *
	 * @param ctx ChannelHandlerContext
	 * @param pkg Msg.ProtoMsg.Message
	 */
	public static void loginSuccess(ChannelHandlerContext ctx, Msg.ProtoMsg.Message pkg) {
		Channel channel = ctx.channel();
		ClientSession session = channel.attr(ClientSession.SESSION_KEY).get();
		session.setSessionId(pkg.getSessionId());
		session.setLoginState(true);
		log.info("user login successfully!");
	}

	public static ClientSession getSession(ChannelHandlerContext ctx) {
		return ctx.channel().attr(ClientSession.SESSION_KEY).get();
	}

	/**
	 *
	 * @return String
	 */
	public String getRemoteAddress() {
		return channel.remoteAddress().toString();
	}

	/**
	 * 写入Protobuf数据
	 * @param pkg Object
	 * @return 异步结果
	 */
	public ChannelFuture writeAndFlush(Object pkg) {
		return channel.writeAndFlush(pkg);
	}

	public void writeAndClose(Object pkg) {
		ChannelFuture future = channel.writeAndFlush(pkg);
		future.addListener(ChannelFutureListener.CLOSE);
	}

	public void close() {
		connectState = false;
		ChannelFuture future = channel.close();

		future.addListener((ChannelFutureListener) future1 -> {
			if (future1.isSuccess()) {
				log.info("connection shutdown successfully!");
			}
		});
	}


}
