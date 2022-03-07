package com.example.common.meta;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ProtoInstant {

	public static final int MAGIC_CODE = 0x86;

	public static final int VERSION_CODE = 0x01;

	/**
	 * 返回码枚举类
	 */
	@AllArgsConstructor
	public enum ResultCodeEnum
	{

		SUCCESS(0, "登陆成功"),
		AUTH_FAILED(1, "登录失败"),
		NO_TOKEN(2, "没有授权码"),
		UNKNOW_ERROR(3, "未知错误"),;

		@Getter
		private Integer code;

		@Getter
		private String desc;
	}
}
