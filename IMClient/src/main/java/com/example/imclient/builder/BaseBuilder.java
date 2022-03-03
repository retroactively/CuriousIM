package com.example.imclient.builder;

import com.example.common.meta.Msg;
import com.example.imclient.session.ClientSession;

public class BaseBuilder {

	protected Msg.ProtoMsg.HeadType type;

	private long seqId;

	private ClientSession session;

	public BaseBuilder(Msg.ProtoMsg.HeadType type, ClientSession session) {
		this.type = type;
		this.session = session;
	}

	/**
	 * 构建基础部分
	 * @param seqId
	 * @return
	 */
	public Msg.ProtoMsg.Message buildCommon(long seqId) {
		this.seqId = seqId;

		Msg.ProtoMsg.Message.Builder builder =
				Msg.ProtoMsg.Message.newBuilder()
						.setType(type)
						.setSequence(seqId)
						.setSessionId(session.getSessionId());
		return builder.buildPartial();
	}
}
