package com.example.common.bean;


import com.example.common.meta.Msg;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Builder
@Data
@Slf4j
public class User implements Serializable {

	String uid;

	String devId;

	String token;

	String nickName;

	transient Platform platform;

	int intPlatform;

	private String sessionId;

	public static User fromMsg(Msg.ProtoMsg.LoginRequest request) {
		User user = User.builder()
				.uid(request.getUid())
				.devId(request.getDeviceId())
				.token(request.getToken())
				.intPlatform(request.getPlatform())
				.build();
		log.info("login in progress : {}", user.toString());
		return user;
	}

	@AllArgsConstructor
	public enum Platform {
		WINDOWS(1, "windows"),
		MAC(2, "mac"),
		ANDRIOD(3, "android"),
		IOS(4, "ios"),
		WEB(5, "web"),
		UNKNOWN(6, "unknown");

		@Getter
		private int code;

		@Getter
		private String msg;

		public static String getMsg(Byte code) {
			for (Platform item: values()) {
				if (item.getCode() == code) {
					return item.getMsg();
				}
			}
			return "";
		}
	}



}
