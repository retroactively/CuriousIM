package com.example.imserver.session;


import com.example.common.bean.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class ServerSession {
	public static final AttributeKey<String> KEY_USER_ID = AttributeKey.valueOf("key_user_id");

	public static final AttributeKey<ServerSession> SESSION_KEY = AttributeKey.valueOf("session_key");

	// 服务端用户会话管理的核心
	private Channel channel;

	@Getter
	private User user;

	@Getter
	private String sessionId;

	@Getter
	private boolean loginState = false;

	private Map<String, Object> map = new HashMap<String, Object>();

	/**
	 * 绑定通道
	 * @param channel Channel
	 */
	public ServerSession(Channel channel) {
		this.channel = channel;
		this.sessionId = this.buildNewSessionId();
	}

	public static ServerSession getSession(ChannelHandlerContext context) {
		Channel channel = context.channel();
		return channel.attr(ServerSession.SESSION_KEY).get();
	}

	private String buildNewSessionId() {
		String uuid = UUID.randomUUID().toString();
		return uuid.replaceAll("-", "");
	}

	// 写ProtoBuf数据帧
	public synchronized void writeAndFlush(Object pkg) {
		channel.writeAndFlush(pkg);
	}

	public void setUser(User user) {
		this.user = user;
		user.setSessionId(sessionId);
	}

	public boolean isValid() {
		return getUser() != null;
	}

	// Session 与 Channel 双向绑定
	public ServerSession bind() {
		log.info("serversession bind session with : " + channel.remoteAddress());
		channel.attr(ServerSession.SESSION_KEY).set(this);
		SessionMap.getInstance().addSession(getSessionId(), this);
		loginState = true;
		return this;
	}

	public static void closeSession(ChannelHandlerContext context) {
		ServerSession session = context.channel().attr(ServerSession.SESSION_KEY).get();
		if ((null != session) && session.isValid()) {
			try {
				session.clone();
			} catch (Exception e) {
				e.printStackTrace();
			}
			SessionMap.getInstance().removeSession(session.getSessionId());
		}
	}

	public synchronized void closeConnection() {
		ChannelFuture future = channel.close();
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture channelFuture) throws Exception {
				if (!future.isSuccess()) {
					log.error("channel close error!");
				}
			}
		});
	}


}
