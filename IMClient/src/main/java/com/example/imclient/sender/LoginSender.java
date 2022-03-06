package com.example.imclient.sender;

import com.example.common.meta.Immsg;
import com.example.imclient.builder.LoginMsgBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginSender extends BaseSender {

	public void sendLoginMsg() {
		if (!isConnected()) {
			log.warn("connection is not established!");
			return;
		}

		log.info("build login msg!");
		Immsg.Message message = LoginMsgBuilder.buildLoginMsg(getUser(), getClientSession());
		log.info("send login msg!");
		super.sendMessage(message);
	}
}
