package com.example.imserver.processor;

import com.example.imserver.builder.LoginResponseBuilder;
import com.example.common.bean.User;
import com.example.common.meta.Msg;
import com.example.common.meta.ProtoInstant;
import com.example.imserver.session.ServerSession;
import com.example.imserver.session.SessionMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class LoginProcessor extends AbstractServerProcessor{

	@Autowired
	private LoginResponseBuilder responseBuilder;

	@Override
	public Msg.ProtoMsg.HeadType getType() {
		return Msg.ProtoMsg.HeadType.LOGIN_REQUEST;
	}

	private boolean checkUser(User user) {
		/**
		 * 校验用户,比较耗时的操作,需要100 ms以上的时间
		 * 方法1：调用远程用户restfull 校验服务
		 * 方法2：调用数据库接口校验
		 */
		return SessionMap.getInstance().isLogin(user);
	}

	@Override
	public boolean action(ServerSession session, Msg.ProtoMsg.Message proto) {
		Msg.ProtoMsg.LoginRequest request = proto.getLoginRequest();
		long seq = proto.getSequence();

		User user = User.fromMsg(request);
		// 已登陆
		if (checkUser(user)) {
			// 登录失败
			ProtoInstant.ResultCodeEnum resultCode = ProtoInstant.ResultCodeEnum.NO_TOKEN;
			Msg.ProtoMsg.Message response = responseBuilder.loginResponse(resultCode, seq, "-1");
			session.writeAndFlush(response);
			return false;
		}

		session.setUser(user);
		session.bind();
		// 登陆成功
		ProtoInstant.ResultCodeEnum resultCode = ProtoInstant.ResultCodeEnum.SUCCESS;
		Msg.ProtoMsg.Message response = responseBuilder.loginResponse(resultCode, seq, session.getSessionId());
		session.writeAndFlush(response);
		return true;
	}
}
