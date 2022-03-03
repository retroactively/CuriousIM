package com.example.imserver.processor;

import com.example.common.meta.Msg;
import com.example.common.session.ServerSession;

public interface ServerProcessor {

	/**
	 * 获取消息类型
	 * @return Msg.ProtoMsg.HeadType
	 */
	Msg.ProtoMsg.HeadType getType();

	/**
	 * 定义Processor的行为
	 * @param session ServerSession
	 * @param proto Msg.ProtoMsg.Message
	 * @return boolean
	 */
	boolean action(ServerSession session, Msg.ProtoMsg.Message proto);
}
