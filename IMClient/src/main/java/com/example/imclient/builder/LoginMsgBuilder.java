package com.example.imclient.builder;

import com.example.common.bean.User;
import com.example.common.meta.Msg;
import com.example.imclient.session.ClientSession;

public class LoginMsgBuilder extends BaseBuilder{

	private User user;

	public LoginMsgBuilder(User user, ClientSession session) {
		super(Msg.ProtoMsg.HeadType.LOGIN_REQUEST, session);
		this.user = user;
	}

	public Msg.ProtoMsg.Message build() {
		Msg.ProtoMsg.Message message = buildCommon(-1);
		Msg.ProtoMsg.LoginRequest.Builder builder = Msg.ProtoMsg.LoginRequest.newBuilder()
				.setDeviceId(user.getDevId())
				.setPlatform(user.getIntPlatform())
				.setToken(user.getToken())
				.setUid(user.getUid());
		return message.toBuilder().setLoginRequest(builder).build();
	}

	public static Msg.ProtoMsg.Message buildLoginMsg(User user, ClientSession session) {
		LoginMsgBuilder builder = new LoginMsgBuilder(user, session);
		return builder.build();
	}
}
