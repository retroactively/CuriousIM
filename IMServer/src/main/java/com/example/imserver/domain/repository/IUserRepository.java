package com.example.imserver.domain.repository;

import com.example.imserver.domain.model.UserInfo;
import com.example.imserver.domain.po.User;

import java.util.List;

public interface IUserRepository {

	/**
	 * 查询用户密码
	 * @param userId
	 * @return
	 */
	String queryUserPassword(String userId);

	/**
	 * 查询用户信息
	 *
	 * @param userId 用户ID
	 * @return 用户信息
	 */
	UserInfo queryUserInfo(String userId);

	/**
	 * 注册新用户
	 * @param user
	 * @return
	 */
	boolean addUser(User user);

	/**
	 * 获取所有用户
	 * @return
	 */
	List<User> getUserList();
}
