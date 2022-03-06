package com.example.imclient.builder;

import com.example.common.bean.UserDTO;
import com.example.common.meta.Immsg;
import com.example.imclient.session.ClientSession;

public class HeartBeatBuilder extends BaseBuilder {

	private final UserDTO user;

	public HeartBeatBuilder(UserDTO user, ClientSession session) {
		super(Immsg.HeadType.MESSAGE_NOTIFICATION, session);
		this.user = user;
	}

	public Immsg.Message buildMsg() {
		Immsg.Message message = buildCommon(-1);
		Immsg.MessageHeartBeat.Builder builder = Immsg.MessageHeartBeat.newBuilder()
				.setSeq(0)
				.setJson("{\"from\":\"client\"}")
				.setUid(user.getUserId());
		return message.toBuilder().setHeartBeat(builder).build();
	}
}
