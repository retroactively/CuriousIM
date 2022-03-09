package com.example.imclient.builder;

import com.example.common.bean.ChatMsg;
import com.example.common.bean.User;
import com.example.common.meta.Immsg;
import com.example.imclient.session.ClientSession;

public class ChatMsgBuilder extends BaseBuilder{

	private ChatMsg chatMsg;

	private User user;


	public ChatMsgBuilder(ChatMsg chatMsg, User user, ClientSession session) {
		super(Immsg.HeadType.MESSAGE_REQUEST, session);
		this.chatMsg = chatMsg;
		this.user = user;
	}

	public Immsg.Message build() {
		Immsg.Message message = buildCommon(-1);
		Immsg.MessageRequest.Builder builder = Immsg.MessageRequest.newBuilder();
		chatMsg.fillMsg(builder);
		return message.toBuilder().setMessageRequest(builder).build();
	}

	public static Immsg.Message buildChatMsg(ChatMsg chatMsg, User user, ClientSession session) {
		ChatMsgBuilder builder = new ChatMsgBuilder(chatMsg, user, session);
		return builder.build();
	}
}
