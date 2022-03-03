package com.example.common.bean;

import com.example.common.meta.Msg;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class UserDTO {
	String userId;

	String userName;

	String devId;

	String token;

	String nickName = "nickName";

	PLATTYPE platform = PLATTYPE.MAC;

	public enum PLATTYPE {
		WINDOWS, MAC, ANDROID, IOS, WEB, OTHER
	}

	private String sessionId;

	public void setPlatform(int platform) {
		PLATTYPE[] values = PLATTYPE.values();

		for (int i = 0; i < values.length; i++) {
			if (values[i].ordinal() == platform) {
				this.platform = values[i];
				return;
			}
		}
	}

	@Override
	public String toString() {
		return "User{" +
				"userId='" + userId + '\'' +
				", devId='" + devId + '\'' +
				", token='" + token + '\'' +
				", nickName='" + nickName + '\'' +
				", platform=" + platform +
				'}';
	}

	public static UserDTO fromMsg(Msg.ProtoMsg.LoginRequest request) {
		UserDTO user = new UserDTO();
		user.setUserId(request.getUid());
		user.setDevId(request.getDeviceId());
		user.setToken(request.getToken());
		user.setPlatform(request.getPlatform());
		log.info("login progress: {}", user.toString());
		return user;
	}
}
