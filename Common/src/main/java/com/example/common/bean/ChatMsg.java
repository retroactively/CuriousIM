package com.example.common.bean;


import com.example.common.meta.Immsg;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

@Data
public class ChatMsg {
	private long msgId;

	private String from;

	private String to;

	private long time;

	private MSGTYPE msgtype;

	private String content;

	private String url;

	private String property;

	// 发送者昵称
	private String fromNick;

	private String json;

	private User user;

	/**
	 * 消息类型  1：纯文本  2：音频 3：视频 4：地理位置 5：其他
	 */
	public enum MSGTYPE {
		TEXT, AUDIO, VIDEO, GEO, OTHER;
	}

	public ChatMsg(User user) {
		if (null == user) {
			return;
		}
		this.user = user;
		this.setTime(System.currentTimeMillis());
		this.setFrom(user.getUserId());
		this.setFromNick(user.getNickName());
	}

	public void fillMsg(Immsg.MessageRequest.Builder builder) {
		BeanUtils.copyProperties(this, builder);
//		if (msgId > 0) {
//			builder.setMsgId(msgId);
//		}
//		if (time > 0) {
//			builder.setTime(time);
//		}
//		if (msgtype != null) {
//			builder.setMsgType(msgtype.ordinal());
//		}
//		if (StringUtils.isNotEmpty(from)) {
//			builder.setFrom(from);
//		}
//		if (StringUtils.isNotEmpty(to)) {
//			builder.setTo(to);
//		}
//		if (StringUtils.isNotEmpty(content)) {
//			builder.setContent(content);
//		}
//		if (StringUtils.isNotEmpty(url)) {
//			builder.setUrl(url);
//		}
//		if (StringUtils.isNotEmpty(property)) {
//			builder.setProperty(property);
//		}
//		if (StringUtils.isNotEmpty(fromNick)) {
//			builder.setProperty(fromNick);
//		}
//		if (StringUtils.isNotEmpty(json)) {
//			builder.setProperty(json);
//		}
	}
}
