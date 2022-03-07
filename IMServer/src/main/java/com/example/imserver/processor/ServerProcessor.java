package com.example.imserver.processor;

import com.example.common.meta.Immsg;
import com.example.imserver.session.ServerSession;

public interface ServerProcessor {

	/**
	 * 获取消息类型
	 * @return Msg.ProtoMsg.HeadType
	 */
	Immsg.HeadType getType();

	/**
	 * 定义Processor的行为
	 * @param session ServerSession
	 * @param proto Msg.ProtoMsg.Message
	 * @return boolean
	 */
	boolean action(ServerSession session, Immsg.Message proto);
}
