package com.example.common.codec;

import com.example.common.exception.InvalidFrameException;
import com.example.common.meta.Msg;
import com.example.common.meta.ProtoInstant;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ProtobufDecoder extends ByteToMessageDecoder {
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
		// 1. 标记当前index位置
		byteBuf.markReaderIndex();
		// 2. 判断是否大于包头
		if (byteBuf.readableBytes() < 8) {
			return;
		}
		short magic = byteBuf.readShort();
		if (magic != ProtoInstant.MAGIC_CODE) {
			String error = "client take the wrong magic number: " + ctx.channel().remoteAddress();
			log.warn(error);
			throw new InvalidFrameException(error);
		}

		short version = byteBuf.readShort();
		int len = byteBuf.readInt();
		// illegal data valid
		if (len < 0) {
			ctx.close();
		}
		// 读到的数据不够
		if (len > byteBuf.readableBytes()) {
			byteBuf.resetReaderIndex();
			return;
		}
		byte[] bytes;
		if (byteBuf.hasArray()) {
			// heap buffer
			ByteBuf slice = byteBuf.slice(byteBuf.readerIndex(), len);
			bytes = slice.array();
			byteBuf.retain();
		} else {
			// direct buffer
			bytes = new byte[len];
			byteBuf.readBytes(bytes, 0, len);
		}

		// bytes to msg
		Msg.ProtoMsg.Message msg = Msg.ProtoMsg.Message.parseFrom(bytes);
		if (byteBuf.hasArray()) {
			byteBuf.release();
		}
		if (null != msg) {
			list.add(msg);
		}
	}
}
