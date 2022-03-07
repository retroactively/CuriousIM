package com.example.imclient.builder;

import com.example.common.meta.Immsg;
import com.example.imclient.session.ClientSession;

public class BaseBuilder {

	protected Immsg.HeadType type;

	private long seqId;

	private ClientSession session;

	public BaseBuilder(Immsg.HeadType type, ClientSession session) {
		this.type = type;
		this.session = session;
	}

	/**
	 * 构建基础部分
	 * @param seqId
	 * @return
	 */
	public Immsg.Message buildCommon(long seqId) {
		this.seqId = seqId;

		Immsg.Message.Builder builder =
				Immsg.Message.newBuilder()
						.setType(type)
						.setSequence(seqId)
						.setSessionId(session.getSessionId());
		return builder.buildPartial();
	}
}
