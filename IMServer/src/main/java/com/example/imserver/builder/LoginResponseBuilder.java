package com.example.imserver.builder;

import com.example.common.meta.Immsg;
import com.example.common.meta.ProtoInstant;
import org.springframework.stereotype.Service;

@Service
public class LoginResponseBuilder {
	public Immsg.Message loginResponse(ProtoInstant.ResultCodeEnum en, long seqId, String sessionId) {
		Immsg.Message.Builder messageBuilder = Immsg.Message.newBuilder()
				.setType(Immsg.HeadType.LOGIN_RESPONSE)
				.setSequence(seqId)
				.setSessionId(sessionId);

		Immsg.LoginResponse.Builder  responseBuilder = Immsg.LoginResponse.newBuilder()
				.setCode(en.getCode())
				.setInfo(en.getDesc())
				.setExpose(1);

		messageBuilder.setLoginResponse(responseBuilder.build());
		return messageBuilder.build();
	}
}
