package com.example.imserver.builder;

import com.example.common.meta.Msg;
import com.example.common.meta.ProtoInstant;
import org.springframework.stereotype.Service;

@Service
public class LoginResponseBuilder {
	public Msg.ProtoMsg.Message loginResponse(ProtoInstant.ResultCodeEnum en, long seqId, String sessionId) {
		Msg.ProtoMsg.Message.Builder messageBuilder = Msg.ProtoMsg.Message.newBuilder()
				.setType(Msg.ProtoMsg.HeadType.LOGIN_RESPONSE)
				.setSequence(seqId)
				.setSessionId(sessionId);

		Msg.ProtoMsg.LoginResponse.Builder  responseBuilder = Msg.ProtoMsg.LoginResponse.newBuilder()
				.setCode(en.getCode())
				.setInfo(en.getDesc())
				.setExpose(1);

		messageBuilder.setLoginResponse(responseBuilder.build());
		return messageBuilder.build();
	}
}
