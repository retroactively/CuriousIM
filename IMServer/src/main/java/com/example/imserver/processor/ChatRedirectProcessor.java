package com.example.imserver.processor;

import com.example.common.meta.Immsg;
import com.example.imserver.session.ServerSession;
import com.example.imserver.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ChatRedirectProcessor extends AbstractServerProcessor{
	@Override
	public Immsg.HeadType getType() {
		return Immsg.HeadType.MESSAGE_REQUEST;
	}

	@Override
	public boolean action(ServerSession session, Immsg.Message proto) {
		// 这里服务端的路由转发不是根据sessionId，而是根据userId。
		// 其原因是因为同一个用户B可能登录了多个会话（桌面会话、移动端会话、网页端会话），
		// 这时发给用户B的聊天消息必须转发到多个会话，所以需要根据userId进行转发。
		Immsg.MessageRequest msg = proto.getMessageRequest();
		String from = msg.getFrom();
		String to = msg.getTo();
		log.info("Chat msg | from: " + from + " , to :" + to + " , content: " + msg.getContent());

		List<ServerSession> sessions = SessionMap.getInstance().getSessionsByUser(to);
		if (null == sessions) {
			log.warn("[" + to + "] is offline, send failed!");
		} else {
			sessions.forEach((s) -> {
				s.writeAndFlush(proto);
			});
		}

		return true;
	}
}
