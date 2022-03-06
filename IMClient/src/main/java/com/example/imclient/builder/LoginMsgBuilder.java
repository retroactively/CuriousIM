package com.example.imclient.builder;

import com.example.common.bean.User;
import com.example.common.meta.Immsg;
import com.example.imclient.session.ClientSession;

public class LoginMsgBuilder extends BaseBuilder{

	private User user;

	public LoginMsgBuilder(User user, ClientSession session) {
		super(Immsg.HeadType.LOGIN_REQUEST, session);
		this.user = user;
	}

	public Immsg.Message build() {
		Immsg.Message message = buildCommon(-1);
		Immsg.LoginRequest.Builder builder = Immsg.LoginRequest.newBuilder()
				.setDeviceId(user.getDevId())
				.setPlatform(user.getIntPlatform())
				.setToken(user.getToken())
				.setUid(user.getUid());
		return message.toBuilder().setLoginRequest(builder).build();
	}

	public static Immsg.Message buildLoginMsg(User user, ClientSession session) {
		LoginMsgBuilder builder = new LoginMsgBuilder(user, session);
		return builder.build();
	}
}
