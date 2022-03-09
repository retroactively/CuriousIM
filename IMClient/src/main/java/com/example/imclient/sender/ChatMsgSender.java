package com.example.imclient.sender;

import com.example.common.bean.ChatMsg;
import com.example.common.meta.Immsg;
import com.example.imclient.builder.ChatMsgBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ChatMsgSender extends BaseSender{

	public void sendChatMessage(String touid, String content) {
		log.info("send msg startContentServer");
		ChatMsg msg = new ChatMsg(getUser());
		msg.setContent(content);
		msg.setMsgtype(ChatMsg.MSGTYPE.TEXT);
		msg.setTo(touid);
		msg.setTime(System.currentTimeMillis());
		Immsg.Message message = ChatMsgBuilder.buildChatMsg(msg, getUser(), getClientSession());
		super.sendMessage(message);
	}

	@Override
	protected void sendSucceed(Immsg.Message message) {
		log.info("发送成功:" + message.getMessageRequest().getContent());
	}

	@Override
	protected void sendFailed(Immsg.Message message) {
		log.info("发送失败:" + message.getMessageRequest().getContent());
	}
}
