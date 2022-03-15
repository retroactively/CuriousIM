package com.example.imserver.domain.repository;

import com.example.imserver.domain.dao.UserMapper;
import com.example.imserver.domain.model.UserInfo;
import com.example.imserver.domain.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserRepository implements IUserRepository{

	@Autowired
	private UserMapper userDao;

	@Override
	public String queryUserPassword(String userId) {
		return userDao.queryUserPassword(userId);
	}

	@Override
	public UserInfo queryUserInfo(String userId) {
		User user = userDao.queryUserById(userId);
		return new UserInfo(user.getUserid(), user.getUsernickname(), user.getUserhead());
	}

	@Override
	public boolean addUser(User user) {
		return userDao.insertSelective(user) != 0;
	}

	@Override
	public List<User> getUserList() {
		return userDao.queryAllUsers();
	}

}
