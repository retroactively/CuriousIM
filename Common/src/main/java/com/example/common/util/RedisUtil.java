package com.example.common.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

public class RedisUtil {

	public static final String USER_MAP_KEY = "chat:user:map";

	private static RedissonClient client;

	public static void connect(String addr, String pwd) {
		Config config = new Config();
		config.setCodec(new org.redisson.client.codec.StringCodec());
		config.useSingleServer().setAddress(addr).setPassword(pwd);
		client = Redisson.create(config);
	}

	public static void disconnect() {
		client.shutdown();
	}

	public static RedissonClient getRedis() {
		return client;
	}

	public static String sessionStore(Long uid) {
		return "chat:user:session:" + uid;
	}
}
