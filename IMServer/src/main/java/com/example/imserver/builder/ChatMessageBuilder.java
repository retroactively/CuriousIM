package com.example.imserver.builder;

import com.example.common.meta.Msg;
import com.example.common.meta.ProtoInstant;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageBuilder {
	public Msg.ProtoMsg.Message chatResponse(long seqId, ProtoInstant.ResultCodeEnum en) {
		Msg.ProtoMsg.Message.Builder msgBuilder = Msg.ProtoMsg.Message.newBuilder()
				.setType(Msg.ProtoMsg.HeadType.MESSAGE_RESPONSE)
				.setSequence(seqId);
		// TODO  change server Msg to Immsg

		Msg.ProtoMsg.MessageResponse.Builder responseBuilder = Msg.ProtoMsg.MessageResponse.newBuilder()
				.setCode(en.getCode())
				.setInfo(en.getDesc())
				.setExpose(1);
		msgBuilder.setMessageResponse(responseBuilder.build());
		return msgBuilder.build();
	}

}
