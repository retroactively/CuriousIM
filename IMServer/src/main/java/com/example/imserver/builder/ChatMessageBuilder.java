package com.example.imserver.builder;

import com.example.common.meta.Immsg;
import com.example.common.meta.ProtoInstant;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageBuilder {
	public Immsg.Message chatResponse(long seqId, ProtoInstant.ResultCodeEnum en) {
		Immsg.Message.Builder msgBuilder = Immsg.Message.newBuilder()
				.setType(Immsg.HeadType.MESSAGE_RESPONSE)
				.setSequence(seqId);
		// TODO  change server Msg to Immsg

		Immsg.MessageResponse.Builder responseBuilder = Immsg.MessageResponse.newBuilder()
				.setCode(en.getCode())
				.setInfo(en.getDesc())
				.setExpose(1);
		msgBuilder.setMessageResponse(responseBuilder.build());
		return msgBuilder.build();
	}

}
