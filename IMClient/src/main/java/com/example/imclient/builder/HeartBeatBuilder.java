package com.example.imclient.builder;

import com.example.common.bean.User;
import com.example.common.bean.UserDTO;
import com.example.common.meta.Immsg;
import com.example.imclient.session.ClientSession;

public class HeartBeatBuilder extends BaseBuilder {

	private final User user;

	public HeartBeatBuilder(User user, ClientSession session) {
		super(Immsg.HeadType.HEART_BEAT, session);
		this.user = user;
	}

	public Immsg.Message buildMsg() {
		Immsg.Message message = buildCommon(-1);
		Immsg.MessageHeartBeat.Builder builder = Immsg.MessageHeartBeat.newBuilder()
				.setSeq(0)
				.setJson("{\"from\":\"client\"}")
				.setUid(user.getUid());
		return message.toBuilder().setHeartBeat(builder).build();
	}
}
