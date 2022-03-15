package com.example.imserver.domain.service;

import com.example.common.util.RedisUtil;
import com.example.imserver.domain.po.User;
import com.example.imserver.domain.repository.IUserRepository;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class UserService implements IUserService {

	@Autowired
	private IUserRepository userRepository;

	private RedissonClient redidsClient;

	@Override
	public void loadUsersCache() {
		List<User> users = userRepository.getUserList();
		redidsClient = RedisUtil.getRedis();
//		RBucket<> = redidsClient.getSet(RedisUtil.USER_MAP_KEY);

	}
}
