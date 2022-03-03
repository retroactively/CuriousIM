package com.example.common.codec;

import com.example.common.meta.Msg;
import com.example.common.meta.ProtoInstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProtobufEncoder extends MessageToByteEncoder<Msg.ProtoMsg.Message> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Msg.ProtoMsg.Message message, ByteBuf byteBuf) throws Exception {
		byteBuf.writeShort(ProtoInstant.MAGIC_CODE);
		byteBuf.writeShort(ProtoInstant.VERSION_CODE);
		// 消息体可以加密
		byte[] bytes = message.toByteArray();

		int len = bytes.length;
		byteBuf.writeInt(len);
		byteBuf.writeBytes(bytes);
		log.info("send " + "[remote ip : " + ctx.channel().remoteAddress()
				+ " ][ total length: " + len + " ][ bare length: " + message.getSerializedSize() + " ]");
	}
}
