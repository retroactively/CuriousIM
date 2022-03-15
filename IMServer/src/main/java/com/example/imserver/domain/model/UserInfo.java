package com.example.imserver.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserInfo {

	//用户ID
	private String userId;

	//用户昵称
	private String userNickName;

	//用户头像
	private String userHead;
}
