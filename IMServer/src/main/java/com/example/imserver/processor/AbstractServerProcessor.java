package com.example.imserver.processor;

import com.example.common.session.ServerSession;
import io.netty.channel.Channel;

public abstract class AbstractServerProcessor implements ServerProcessor{

	protected String getKey(Channel channel) {
		return channel.attr(ServerSession.KEY_USER_ID).get();
	}

	protected void setKey(Channel channel, String key) {
		channel.attr(ServerSession.KEY_USER_ID).set(key);
	}

	protected void checkAuth(Channel channel) throws Exception {
		if (null == getKey(channel)) {
			throw new Exception("this user, login failed.");
		}
	}

}
