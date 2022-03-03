package com.example.common.builder;

import com.example.common.bean.UserDTO;
import com.example.common.meta.Msg;
import com.example.common.session.ClientSession;
import com.google.protobuf.ByteString;

public class HeartBeatBuilder extends BaseBuilder {

	private final UserDTO user;

	public HeartBeatBuilder(UserDTO user, ClientSession session) {
		super(Msg.ProtoMsg.HeadType.MESSAGE_NOTIFICATION, session);
		this.user = user;
	}

	public Msg.ProtoMsg.Message buildMsg() {
		Msg.ProtoMsg.Message message = buildCommon(-1);
		Msg.ProtoMsg.MessageNotification.Builder builder = Msg.ProtoMsg.MessageNotification.newBuilder()
				.setMsgType(5)
				.setSender(ByteString.copyFrom(user.getUserId().getBytes()))
				.setJson("{\"from\":\"client\"}");
		return message.toBuilder().setMessageNotification(builder).build();
	}
}
